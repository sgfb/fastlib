package com.fastlib.adapter;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fastlib.base.OldViewHolder;
import com.fastlib.interf.AdapterViewState;
import com.fastlib.net.Listener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;
import com.fastlib.net.Result;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 16/7/24.
 */
public abstract class MultiTypeAdapter extends BaseAdapter implements Listener{
    protected Context mContext;
    protected List<Request> mRequest;
    private AdapterViewState mViewState;
    protected List<ObjWithType> mData;
    protected Map<Request,Integer> mRequestIndex;
    private Map<Integer,Integer> mLayoutId;
    private SwipeRefreshLayout mRefreshLayout;
    private int mPerCount; //每次读取条数，默认为1
    protected int mCurrentRequestIndex;
    protected boolean isRefresh,isMore,isLoading,isSaveCache;

    public abstract void binding(int position,ObjWithType owy,OldViewHolder holder); //绑定视图
    public abstract List<ObjWithType> translate(Request r,String result); //服务器拉取的数据转化
    public abstract Map<Integer,Integer> getLayoutId(); //获取不同类型的布局id
    public abstract List<Request> getRequest();

    /**
     * 请求更多数据时的请求
     */
    public abstract void getMoreDataRequest();

    /**
     * 刷新数据时的请求
     */
    public abstract void getRefreshDataRequest();

    public MultiTypeAdapter(Context context){
        this(context,true);
    }

    public MultiTypeAdapter(Context context,boolean start){
        mContext=context;
        mRequestIndex=new HashMap<>();
        mRequest=getRequest();
        mPerCount=1;
        isRefresh=false;
        isMore=true;
        isLoading=false;
        mLayoutId=getLayoutId();
        for(Request r:mRequest) {
            r.setListener(this);
            mRequestIndex.put(r,0);
        }
        if(start)
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

    public void setRefresh(SwipeRefreshLayout refresh){
        mRefreshLayout=refresh;
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
        mCurrentRequestIndex=0;
        Iterator<Request> iter=mRequestIndex.keySet().iterator();
        while(iter.hasNext())
            mRequestIndex.put(iter.next(),0);
        getRefreshDataRequest();
        NetQueue.getInstance().netRequest(mRequest.get(0));
    }

    /**
     * 获取更多数据
     */
    private void loadMoreData(){
        isLoading=true;
        isRefresh=false;
        if(mViewState!=null)
            mViewState.onStateChanged(AdapterViewState.STATE_LOADING);
        getMoreDataRequest();
        NetQueue.getInstance().netRequest(mRequest.get(mCurrentRequestIndex));
    }

    private OldViewHolder getViewHolder(int position, View convertView, ViewGroup parent){
        return OldViewHolder.get(mContext, convertView, parent,mLayoutId.get(getItemViewType(position)));
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
    public void onResponseListener(Request r,String result){
        if(mRefreshLayout!=null)
            mRefreshLayout.setRefreshing(false);
        List<ObjWithType> list=translate(r,result);

        isLoading=false;
        if(list==null||list.size()<=Math.min(0,mPerCount)){ //如果为true说明当前request已全部接收数据完毕了,尝试跳到下一个request中
            mCurrentRequestIndex++;
            if(mCurrentRequestIndex>=mRequest.size()){
                isMore=false;
                if(mViewState!=null)
                    mViewState.onStateChanged(AdapterViewState.STATE_NO_MORE);
            }
        }
        if(isRefresh)
            mData = list;
        else{
            int index=mRequest.indexOf(r);
            if(index==mRequest.size()-1)
                mData.addAll(list);
            else {
                int listIndex = 0;
                for (int i = 0; i <=index; i++)
                    listIndex += mRequestIndex.get(mRequest.get(i));
                mData.addAll(listIndex,list);
            }
        }
        if(r!=null)
            mRequestIndex.put(r,mRequestIndex.get(r)+list.size());
        notifyDataSetChanged();
    }

    @Override
    public void onErrorListener(Request r,String error){
        if(mRefreshLayout!=null)
            mRefreshLayout.setRefreshing(false);
        System.out.println("MultiTypeAdapter error:" + error);
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
