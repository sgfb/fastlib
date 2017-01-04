package com.fastlib.app;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.fastlib.annotation.Bind;
import com.fastlib.bean.PermissionRequest;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by sgfb on 16/9/5.
 * Activity基本封装
 */
public class FastActivity extends AppCompatActivity{
    private Thread mMainThread;
    private Map<String,PermissionRequest> mPermissionMap=new HashMap<>();
    protected ThreadPoolExecutor mThreadPool= (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mMainThread =Thread.currentThread();
        mThreadPool.execute(new Runnable(){
            @Override
            public void run(){
                registerEvents();
            }
        });
    }

    /**
     * 启动网络请求
     * @param request
     */
    protected void net(Request request){
        NetQueue.getInstance().netRequest(mThreadPool,request.setHost(this));
    }

    /**
     * 启动一个任务链
     * @param tc
     */
    protected void startTasks(TaskChain tc){
        TaskChain.processTaskChain(this,mThreadPool, mMainThread,tc);
    }

    /**
     * 6.0后请求权限
     * @param permission
     * @param grantedAfterProcess
     * @param deniedAfterProcess
     */
    protected void requestPermission(String permission,Runnable grantedAfterProcess,Runnable deniedAfterProcess){
        if(ContextCompat.checkSelfPermission(this,permission)== PackageManager.PERMISSION_GRANTED)
            grantedAfterProcess.run();
        else{
            if(!mPermissionMap.containsKey(permission)){
                int requestCode= mPermissionMap.size()+1;
                mPermissionMap.put(permission,new PermissionRequest(requestCode,grantedAfterProcess,deniedAfterProcess));
                ActivityCompat.requestPermissions(this, new String[]{permission},requestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int i=0;i<permissions.length;i++){
            PermissionRequest pr= mPermissionMap.remove(permissions[i]);
            if(pr!=null){
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED)
                    pr.hadPermissionProcess.run();
                else
                    pr.deniedPermissionProcess.run();
                break;
            }
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        injectViewEvent();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        injectViewEvent();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        injectViewEvent();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        EventObserver.getInstance().unsubscribe(this);
        mThreadPool.shutdownNow();
    }

    /**
     * 注册方法中的广播事件,如果有
     */
    private void registerEvents(){
        EventObserver.getInstance().subscribe(this);
    }

    /**
     * 运行一段代码如果有异常自行处理
     * @param runOnWorkThread
     * @param m
     * @param objs
     */
    private boolean invokeWithoutError(boolean runOnWorkThread,final Method m,final Object... objs){
        if(runOnWorkThread)
            mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        m.invoke(FastActivity.this,objs);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            });
        else
            try {
                Object result=m.invoke(FastActivity.this,objs);
                if(result instanceof Boolean)
                    return (Boolean)result;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return false;
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                return false;
            }
        return false;
    }

    private void bindListener(final Method m, View v, final Bind vi){
        switch(vi.bindType()){
            case CLICK:
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v){
                        invokeWithoutError(vi.runOnWorkThread(),m,v);
                    }
                });
                break;
            case LONG_CLICK:
                v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v){
                        return invokeWithoutError(vi.runOnWorkThread(),m,v);
                    }
                });
                break;
            case ITEM_CLICK:
                ((AdapterView)v).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        invokeWithoutError(vi.runOnWorkThread(),m,parent,view,position,id);
                    }
                });
                break;
            case ITEM_LONG_CLICK:
                ((AdapterView)v).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        return invokeWithoutError(vi.runOnWorkThread(),m,parent,view,position,id);
                    }
                });
                break;
            default:
                break;
        }
    }

    private void injectViewEvent(){
        //绑定控件方法
        Method[] methods=getClass().getDeclaredMethods();
        if(methods!=null&&methods.length>0)
            for(final Method m:methods){
                Bind vi=m.getAnnotation(Bind.class);
                if(vi!=null){
                    int[] ids=vi.value();
                    if(ids.length>0){
                        for(int id:ids){
                            View v=findViewById(id);
                            if(v!=null)
                                bindListener(m,v,vi);
                        }
                    }
                }
            }

        //绑定视图到属性
        Field[] fields=getClass().getDeclaredFields();
        if(fields!=null&&fields.length>0)
            for(Field field:fields){
                Bind vi=field.getAnnotation(Bind.class);
                if(vi!=null){
                    int[] ids=vi.value();
                    if(ids.length>0){
                        try {
                            field.setAccessible(true);
                            field.set(FastActivity.this,findViewById(ids[0]));
                        } catch (IllegalAccessException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
            }
    }
}
