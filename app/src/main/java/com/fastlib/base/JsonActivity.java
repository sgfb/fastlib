package com.fastlib.base;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.fastlib.app.FastActivity;
import com.fastlib.base.Refreshable;
import com.fastlib.net.Listener;
import com.fastlib.net.Request;
import com.fastlib.utils.JsonBinder;
import com.fastlib.utils.FastJson;

import java.io.IOException;
import java.util.Map;

/**
 * Created by sgfb on 16/9/22.
 * 解析来自服务器json数据界面,支持多重接口
 */
public abstract class JsonActivity extends FastActivity implements Listener,Refreshable.RefreshCallback{
    private Refreshable mRefresh; //刷新布局
    private Request[] mRequests;
    protected JsonBinder mJsonBinder;

    /**
     * 生成接口请求组
     * @return 请求组
     */
    public abstract Request[] generateRequests();

    private void init(){
        mJsonBinder =new JsonBinder(this);
        if(mRequests!=null&&mRequests.length>0){
            for(Request r:mRequests) {
                addRequest(r);
                r.setListener(this);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mRequests= generateRequests();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        init();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        init();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view,params);
        init();
    }

    public void setRefreshLayout(Refreshable refresh){
        mRefresh=refresh;
        mRefresh.setRefreshCallback(new Refreshable.RefreshCallback() {
            @Override
            public void startRefresh() {
                refresh();
            }
        });
    }

    @Override
    public void onResponseListener(Request r, String result){
        if(mRefresh!=null)
            mRefresh.setRefreshStatus(false);
        try {
            Object obj = FastJson.fromJson(result);
            if(obj instanceof Map<?,?>)
                mJsonBinder.fromMapData(findViewById(android.R.id.content), (Map<String, Object>) obj);
        } catch (IOException e){
            System.out.println("解析json时异常:"+result);
        }
    }

    @Override
    public void onErrorListener(Request r, String error){
        if(mRefresh!=null)
            mRefresh.setRefreshStatus(false);
        System.out.println("网络请求失败:"+error);
    }

    @Override
    public void startRefresh(){
        refresh();
    }

    @Override
    public void refresh(){
        super.refresh();
        if(mRefresh!=null)
            mRefresh.setRefreshStatus(true);
    }
}
