package com.fastlib.base;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fastlib.adapter.BindingJsonAdapter;
import com.fastlib.app.FastActivity;
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
 * 绑定来自服务器的json数据填充
 */
public abstract class JsonActivity extends FastActivity implements Listener{
    private View mContentView; //标准视图
    private ListView mList;
    private Refreshable mRefresh;
    private BindingJsonAdapter mAdapter;
    private Request mContentRequest;
    private int mContentId,mRefreshId; //如果是头部，contentId就是layoutId如果不是就是viewId.优先判断非头部
    protected JsonBinder mContentBinding;

    /**
     * 生成列表适配器
     * @return
     */
    public abstract BindingJsonAdapter generateAdapter();

    /**
     * 头部数据接口请求,如果不存在可能使用适配器获取方案.可空
     * @return
     */
    public abstract Request generateContentRequest();

    /**
     * 当带头部数据接口或是对应头部接口返回时手动填充(已自动填充一次)
     * @param contentView
     */
    public abstract void inflaterContent(View contentView);

    public JsonActivity(){

    }

    public JsonActivity(int contentId,@IdRes int refreshId){
        mContentId=contentId;
        mRefreshId=refreshId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mContentRequest = generateContentRequest();
        mAdapter=generateAdapter();
        if(mContentRequest!=null){
            addRequest(mContentRequest);
            mContentRequest.setListener(this);
        }
        if(mAdapter!=null) {
            addRequest(mAdapter.getRequest());
            //适配器接口取额外数据
            mAdapter.setDataCallback(new BindingJsonAdapter.RemoteCallback() {

                @Override
                public void rawData(Object raw) {
                    if(mRefresh!=null)
                        mRefresh.setRefreshStatus(false);
                }

                @Override
                public void standardData(List<Object> data) {

                }

                @Override
                public void extraData(Object data){
                    //强制规范？
                    if (data instanceof Map<?, ?>)
                        mContentBinding.fromMapData(mContentView, (Map<String, Object>) data);
                }

                @Override
                public void error(String msg){
                    if(mRefresh!=null)
                        mRefresh.setRefreshStatus(false);
                }
            });
        }
    }

    private void init(){
        boolean isHead=false;
        mContentView=findViewById(mContentId);
        if(mContentView==null) {
            try{
                mContentView = LayoutInflater.from(this).inflate(mContentId,null);
            }catch(Resources.NotFoundException e){
                //do noting
            }
            if(mContentView!=null)
                isHead=true;
        }
        View refreshView=findViewById(mRefreshId);
        mList=(ListView)findViewById(android.R.id.list);
        if(refreshView instanceof Refreshable) {
            mRefresh = (Refreshable) refreshView;
            mRefresh.setRefreshCallback(new Refreshable.RefreshCallback() {
                @Override
                public void startRefresh(){
                    refresh();
                }
            });
        }
        if(mContentView!=null)
            mContentBinding = new JsonBinder(this, mContentView);
        if(mList!=null){
            if(isHead)
                mList.addHeaderView(mContentView);
            mList.setAdapter(mAdapter);
        }
        if(mContentRequest!=null)
            NetQueue.getInstance().netRequest(mContentRequest);
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
        if(mContentView ==null)
            return;
        if(mRefresh!=null)
            mRefresh.setRefreshStatus(false);
        try {
            Object obj=FastJson.fromJson(result);
            if(obj!=null&&obj instanceof Map<?,?>)
                mContentBinding.fromMapData(mContentView, (Map<String, Object>) obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        inflaterContent(mContentView);
    }

    @Override
    public void onErrorListener(Request r, String error) {
        if(mRefresh!=null)
            mRefresh.setRefreshStatus(false);
        N.showShort(this,"网络错误,请检查网络配置");
        System.out.println("网络错误:"+error);
    }
}