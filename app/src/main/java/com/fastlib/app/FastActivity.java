package com.fastlib.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.fastlib.annotation.ViewInject;
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
    private Map<String,Request> mRequests;
    private ActivityRefreshListener mRefreshListener;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Request[] rs=FastApplication.getInstance().getRequestFromModule(FastActivity.class.getCanonicalName());
        mRequests=new HashMap<>();
        if(rs!=null){
            for(Request r:rs){
                r.setHost(this);
                mRequests.put(r.getUrl(), r);
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
        EventObserver2.getInstance().unsubscribe(this);
        if(mRequests!=null){
            Iterator<String> iter=mRequests.keySet().iterator();
            while(iter.hasNext()){
                String key=iter.next();
                mRequests.get(key).cancel();
            }
        }
    }

    private void injectViewEvent(){
        Method[] methods=getClass().getDeclaredMethods();
        if(methods!=null&&methods.length>0)
        for(final Method m:methods){
            ViewInject vi=m.getAnnotation(ViewInject.class);
            if(vi!=null){
                int[] ids=vi.id();
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
                ViewInject vi=field.getAnnotation(ViewInject.class);
                if(vi!=null){
                    int[] ids=vi.id();
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

    /**
     * 启动一个存在的指定url请求
     * @param url
     */
    public void startNet(String url){
        Request r=mRequests.get(url);
        if(r!=null)
            NetQueue.getInstance().netRequest(r);
    }

    /**
     * 获取指定url请求,可能为null
     * @param url
     * @return
     */
    public Request getRequest(String url){
        return mRequests.get(url);
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
