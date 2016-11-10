package com.fastlib.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.Event;
import com.fastlib.net.Listener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sgfb on 16/9/5.
 */
public class FastActivity extends AppCompatActivity{
    private Map<String,Request> mRequests; //这个activity中的所有网络请求
    private ActivityRefreshListener mRefreshListener;
    private boolean mMutexRunning=false; //互斥网络请求是否运行中

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mRequests=new HashMap<>();
        registerEvents();
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
        if(mRequests!=null){
            Iterator<String> iter=mRequests.keySet().iterator();
            while(iter.hasNext()){
                String key=iter.next();
                Request r=mRequests.get(key);
                if(r.getType()== Request.RequestType.DEFAULT)
                    r.cancel();
            }
        }
    }

    /**
     * 注册方法中的广播事件,如果有
     */
    private void registerEvents(){
        Method[] methods=getClass().getDeclaredMethods();
        if(methods!=null&&methods.length>0)
            for(Method m:methods){
                Event em=m.getAnnotation(Event.class);
                if(em!=null){ //如果EventMethod注解非空，说明这个是一个广播方法
                    Class<?>[] clas=m.getParameterTypes();
                    if(clas==null||clas.length!=1) //如果形参空或长度不为1跳过(这也许不是一个标准的广播方法)
                        continue;
                    EventObserver.getInstance().subscribe(this,clas[0]);
                }
            }
    }

    private void injectViewEvent(){
        Method[] methods=getClass().getDeclaredMethods();
        if(methods!=null&&methods.length>0)
        for(final Method m:methods){
            Bind vi=m.getAnnotation(Bind.class);
            if(vi!=null){
                int[] ids=vi.value();
                if(ids!=null&&ids.length>0){
                    for(int id:ids){
                        View v=findViewById(id);
                        if(v!=null)
                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        m.invoke(FastActivity.this,v);
                                    } catch (IllegalAccessException e){

                                    } catch (InvocationTargetException e){

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
                    if(ids!=null&&ids.length>0){
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

    public void refresh(){
        if(mRefreshListener!=null)
            mRefreshListener.refresh();
        if(mRequests!=null){
            Iterator<String> iter=mRequests.keySet().iterator();
            while(iter.hasNext()){
                String key=iter.next();
                Request request=mRequests.get(key);
                NetQueue.getInstance().netRequest(request);
            }
        }
    }

    public void addRequest(Request request){
        mRequests.put(request.getUrl(),request);
    }

    /**
     * 将请求存储到列表中并且发起请求
     * @param r
     */
    public void net(Request r){
        if(mMutexRunning)
            return;
        if(mRequests.get(r.getUrl())==null)
            mRequests.put(r.getUrl(),r);
        NetQueue.getInstance().netRequest(r);
    }

    /**
     * 启动一个互斥网络请求，当这个请求开始时当前模块不接受其他网络请求，也不会存起来
     * @param view
     * @param request
     */
    public void startMutexRequest(@Nullable final View view, Request request){
        mMutexRunning=true;
        if(view!=null)
            view.setEnabled(false);
        //如果activity被销毁Listener不会回调，当前的需求可以使用Listener回调
        final Listener listener=request.getListener();
        if(listener!=null){
            request.setListener(new Listener() {
//                @Override
//                public void onResponseListener(Request r, String result){
//                    if(view!=null)
//                        view.setEnabled(true);
//                    mMutexRunning=false;
//                    listener.onResponseListener(r,result);
//                }

                @Override
                public void onResponseListener(Request r, Object result) {

                }

                @Override
                public void onErrorListener(Request r, String error){
                    if(view!=null)
                        view.setEnabled(true);
                    mMutexRunning=false;
                    listener.onErrorListener(r,error);
                }
            });
        }
    }

    public void setRefreshListener(ActivityRefreshListener l){
        mRefreshListener=l;
    }

    /**
     * activity整个模块的网络请求刷新回调
     */
    public interface ActivityRefreshListener{
        void refresh();
    }
}
