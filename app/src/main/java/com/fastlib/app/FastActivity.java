package com.fastlib.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.fastlib.annotation.Bind;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by sgfb on 16/9/5.
 */
public class FastActivity extends AppCompatActivity{
    private Thread mMainThread;
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

    protected void net(Request request){
        NetQueue.getInstance().netRequest(mThreadPool,request.setHost(this));
    }

    protected void startTasks(TaskChain tc){
        TaskChain.processTaskChain(this,mThreadPool, mMainThread,tc);
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

    private void injectViewEvent(){
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
                                v.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            m.invoke(FastActivity.this,v);
                                        } catch (IllegalAccessException e){
                                            System.out.println("IllegalAccessException:"+e.getMessage());
                                        } catch (InvocationTargetException e){
                                            System.out.println("InvocationTargetException:"+e.getMessage());
                                        }
                                    }
                                });
                        }
                    }
                }
            }
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
