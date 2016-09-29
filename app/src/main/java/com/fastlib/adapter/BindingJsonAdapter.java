package com.fastlib.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fastlib.base.OldViewHolder;
import com.fastlib.base.Refreshable;
import com.fastlib.db.RemoteCacheServer;
import com.fastlib.base.AdapterViewState;
import com.fastlib.net.Listener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;
import com.fastlib.utils.JsonBinder;
import com.fastlib.utils.FastJson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 16/9/2<br>
 * 从服务器获取到的数据自动填充到对应的view<br>
 * 1.需要有相应的ViewResolve(数据对视图解析器)<br>
 * 2.需要view的id与接口字段名对齐
 */
public abstract class BindingJsonAdapter extends BaseAdapter implements Listener{
    private int mItemLayoutId;
    private int mPerCount; //每次读取条数，默认为1
    protected boolean isRefresh,isMore,isLoading,isSaveCache;
    protected Context mContext;
    protected Request mRequest;
    protected JsonBinder mResolver;
    private AdapterViewState mViewState;
    private RemoteCacheServer mRemoteCacheServer;
    private RemoteCallback mCallback;
    protected List<Object> mResult; //接口数据树
    protected List<Object> mData;

    public abstract Request getRequest();

    /**
     * 请求更多数据时的请求
     * @param request
     */
    public abstract void getMoreDataRequest(Request request);

    /**
     * 刷新数据时的请求
     * @param request
     */
    public abstract void getRefreshDataRequest(Request request);

    public BindingJsonAdapter(Context context, int layoutId){
        mContext=context;
        mItemLayoutId=layoutId;
        mRequest=getRequest();
        mResolver=new JsonBinder(context,LayoutInflater.from(context).inflate(layoutId,null));
        mRequest.setListener(this);
        mRemoteCacheServer =new RemoteCacheServer(mRequest);
        refresh();
    }

    /**
     * 手动绑定视图
     * @param position
     * @param data
     * @param holder
     */
    public void binding(int position,Object data,OldViewHolder holder){

    }

    /**
     * 获取额外数据(不包括状态).通常是列表头部
     * @return 额外数据
     */
    public Object getExtra(Object raw){
        return null;
    }

    @Override
    public int getCount() {
        return mData==null?0:mData.size();
    }

    @Override
    public Object getItem(int position){
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final OldViewHolder viewHolder = getViewHolder(convertView, parent);
        if(position>=getCount()-1&&isMore&&!isLoading)
            loadMoreData();
        Object obj=getItem(position);
        if(obj!=null&&obj instanceof Map<?,?>)
            mResolver.fromMapData(viewHolder.getConvertView(),(Map<String, Object>) obj);
        binding(position,getItem(position),viewHolder);
        return viewHolder.getConvertView();
    }

    private OldViewHolder getViewHolder(View convertView, ViewGroup parent) {
        return OldViewHolder.get(mContext, convertView, parent, mItemLayoutId);
    }

    /**
     * 将原始数据中的列表数据过滤出来.这个方法通常都是要重写的
     * @param rawData
     * @return
     */
    public List<Object> handleRawData(Object rawData){
        return (List<Object>)rawData;
    }

    /**
     * 向服务器请求的参数
     */
    private void loadMoreData(){
        isLoading=true;
        isRefresh=false;
        if(mViewState!=null)
            mViewState.onStateChanged(AdapterViewState.STATE_LOADING);
        getMoreDataRequest(mRequest);
        if(isSaveCache)
            mRemoteCacheServer.loadMore(mRequest.getParams());
        else
            NetQueue.getInstance().netRequest(mRequest);
    }

    public void refresh(){
        isLoading=true;
        isRefresh=true;
        //刷新之后也许有更多数据？
        isMore=true;
        getRefreshDataRequest(mRequest);
        if(isSaveCache)
            mRemoteCacheServer.start();
        else
            NetQueue.getInstance().netRequest(mRequest);
    }

    @Override
    public void onResponseListener(Request r, String result){
        Object obj=null;
        List<Object> dataList;
        try {
            obj = FastJson.fromJson(result);
        } catch (IOException e){
            //do noting
        }

        isLoading=false;
        if(obj==null){
            isMore=false;
            return;
        }
        dataList=handleRawData(obj);
        if(dataList==null||dataList.size()<=0){ //如果解析后的列表数据空,置没有更多数据标志
            isMore=false;
            if(mViewState!=null)
                mViewState.onStateChanged(AdapterViewState.STATE_NO_MORE);
            return;
        }
        if(mCallback!=null){
            mCallback.rawData(obj);
            mCallback.standardData(dataList);
            mCallback.extraData(getExtra(obj));
        }
        if(dataList.size()<mPerCount){
            isMore=false;
            if(mViewState!=null)
                mViewState.onStateChanged(AdapterViewState.STATE_NO_MORE);
        }
        if(isRefresh){
            mResult =new ArrayList<>();
            mData=dataList;
        }
        else mData.addAll(dataList);
        mResult.add(obj);
        notifyDataSetChanged();
    }

    @Override
    public void onErrorListener(Request r, String error){
        isLoading=false;
        if(mCallback!=null)
            mCallback.error(error);
        System.out.println("BindingAdapter error:" + error);
    }

    public void setViewStateListener(AdapterViewState state){
        mViewState=state;
    }

    public void setLoadCount(int count){
        mPerCount=count;
    }

    public boolean isSaveCache(){
        return isSaveCache;
    }

    public void setIsSaveCache(boolean isSaveCache) {
        this.isSaveCache = isSaveCache;
    }

    public void setDataCallback(RemoteCallback callback){
        mCallback=callback;
    }

    /**
     * 当从服务器返回时回调
     */
    public interface RemoteCallback{
        /**
         * 原始数据
         * @param raw
         */
        void rawData(Object raw);

        /**
         * 过滤后的列表数据
         * @param data
         */
        void standardData(List<Object> data);

        /**
         * 其他数据
         * @param data
         */
        void extraData(Object data);

        /**
         * 错误时回调
         * @param msg
         */
        void error(String msg);
    }
}
