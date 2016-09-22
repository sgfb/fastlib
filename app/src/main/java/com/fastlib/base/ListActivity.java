package com.fastlib.base;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fastlib.adapter.BindingJsonAdapter;
import com.fastlib.app.FastActivity;
import com.fastlib.base.Refreshable;
import com.fastlib.net.Listener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;
import com.fastlib.utils.JsonBinder;
import com.fastlib.utils.FastJson;
import com.fastlib.utils.N;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 16/9/21.
 * 带头部列表模块.默认json填充
 */
public abstract class ListActivity extends FastActivity implements Listener{
    private View mHead;
    private ListView mList;
    private Refreshable mRefresh;
    private BindingJsonAdapter mAdapter;
    private Request mHeadRequest;
    private int mHeadId,mRefreshId;
    protected JsonBinder binding;

    public abstract BindingJsonAdapter generateAdapter();

    /**
     * 头部数据接口请求,如果不存在可能使用适配器获取方案.可空
     * @return
     */
    public abstract Request generateHeadRequest();

    /**
     * 当带头部数据接口或是对应头部接口返回时手动填充(已自动填充一次)
     * @param head
     */
    public abstract void inflaterHead(View head);

    public ListActivity(@LayoutRes int headId,@IdRes int refreshId){
        mHeadId=headId;
        mRefreshId=refreshId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mHeadRequest= generateHeadRequest();
        mAdapter= generateAdapter();
        addRequest(mAdapter.getRequest());
        if(mHeadRequest!=null){
            addRequest(mHeadRequest);
            mHeadRequest.setListener(this);
        }
        //适配器接口取额外数据
        mAdapter.setDataCallback(new BindingJsonAdapter.DataCallback() {
            @Override
            public void rawData(Object raw) {

            }

            @Override
            public void standardData(List<Object> data){

            }

            @Override
            public void extraData(Object data){
                if(data instanceof Map<?,?>){
                    binding.fromMapData(mHead, (Map<String, Object>) data);
                }
            }
        });
    }

    private void init(){
        mHead= LayoutInflater.from(this).inflate(mHeadId,null);
        View refreshView=findViewById(mRefreshId);
        mList= (ListView) findViewById(android.R.id.list);
        if(refreshView instanceof Refreshable)
            mRefresh= (Refreshable) refreshView;
        if(mHead!=null) {
            binding = new JsonBinder(this, mHead);
            mList.addHeaderView(mHead);
        }
        if(mHeadRequest!=null)
            NetQueue.getInstance().netRequest(mHeadRequest);
        mAdapter.setRefreshLayout(mRefresh);
        mList.setAdapter(mAdapter);
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

    public void setRefresh(int viewId){
        View refreshView=findViewById(viewId);
        if(refreshView instanceof Refreshable)
            mRefresh= (Refreshable) refreshView;
    }

    @Override
    public void onResponseListener(Request r, String result){
        if(mHead==null)
            return;
        if(mRefresh!=null)
            mRefresh.setRefreshStatus(false);
        try {
            Object obj=FastJson.fromJson(result);
            if(obj!=null&&obj instanceof Map<?,?>)
                binding.fromMapData(mHead, (Map<String, Object>) obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        inflaterHead(mHead);
    }

    @Override
    public void onErrorListener(Request r, String error) {
        if(mRefresh!=null)
            mRefresh.setRefreshStatus(false);
        N.showShort(this,"网络错误,请检查网络配置");
        System.out.println("网络错误:"+error);
    }
}