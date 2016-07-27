package com.fastlib.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fastlib.base.OldViewHolder;
import com.fastlib.interf.AdapterViewState;
import com.fastlib.net.Listener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;
import com.fastlib.net.Result;

import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 16/7/24.
 */
public abstract class MultiTypeAdapter extends BaseAdapter implements Listener{
    protected Context mContext;
    protected Request mRequest;
    private AdapterViewState mViewState;
    protected List<ObjWithType> mData;
    private Map<Integer,Integer> mLayoutId;
    private int mPerCount; //每次读取条数，默认为1
    protected boolean isRefresh,isMore,isLoading,isSaveCache;

    public abstract void binding(int position,ObjWithType owy,OldViewHolder holder); //绑定视图
    public abstract List<ObjWithType> translate(Result result); //服务器拉取的数据转化
    public abstract Map<Integer,Integer> getLayoutId(); //获取不同类型的布局id

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

    public MultiTypeAdapter(Context context,Request request){
        mContext=context;
        mRequest=request;
        mRequest.setListener(this);
        mPerCount=1;
        isRefresh=false;
        isMore=true;
        isLoading=false;
        mLayoutId=getLayoutId();
        refresh();
    }

    @Override
    public int getCount(){
        return mData==null?0:mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final OldViewHolder viewHolder = getViewHolder(position, convertView, parent);
        if(position>=getCount()-1&&isMore&&!isLoading)
            loadMoreData();
        binding(position, (ObjWithType) getItem(position), viewHolder);
        return viewHolder.getConvertView();
    }

    /**
     * 刷新
     */
    public void refresh(){
        isLoading=true;
        isRefresh=true;
        //刷新之后也许有更多数据？
        isMore=true;
        getRefreshDataRequest(mRequest);
        NetQueue.getInstance().netRequest(mRequest);
    }

    /**
     * 获取更多数据
     */
    private void loadMoreData(){
        isLoading=true;
        isRefresh=false;
        if(mViewState!=null)
            mViewState.onStateChanged(AdapterViewState.STATE_LOADING);
        getMoreDataRequest(mRequest);
        NetQueue.getInstance().netRequest(mRequest);
    }

    private OldViewHolder getViewHolder(int position, View convertView, ViewGroup parent){
        return OldViewHolder.get(mContext, convertView, parent, mLayoutId.get(getItemViewType(position)));
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).type;
    }

    public void setData(List<ObjWithType> list){
        mData=list;
        notifyDataSetChanged();
    }

    @Override
    public void onResponseListener(Result result){
        List<ObjWithType> list=translate(result);

        isLoading=false;
        if(list==null||list.size()<=0){
            isMore=false;
            if(mViewState!=null)
                mViewState.onStateChanged(AdapterViewState.STATE_NO_MORE);
            return;
        }
        if(list.size()<mPerCount){
            isMore = false;
            if(mViewState!=null)
                mViewState.onStateChanged(AdapterViewState.STATE_NO_MORE);
        }
        if(isRefresh)
            mData = list;
        else
            mData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onErrorListener(String error) {
        System.out.println("BindingAdapter error:" + error);
    }

    public static class ObjWithType{
        public int type;
        public Object obj;

        public ObjWithType(int type,Object obj){
            this.type=type;
            this.obj=obj;
        }
    }
}
