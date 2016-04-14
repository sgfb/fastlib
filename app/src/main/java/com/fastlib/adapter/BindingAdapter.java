package com.fastlib.adapter;

import java.util.List;

import com.fastlib.base.OldViewHolder;
import com.fastlib.db.DataCache;
import com.fastlib.interf.AdapterViewState;
import com.fastlib.net.Listener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;
import com.fastlib.net.Result;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 绑定适配器，将视图与服务器中的数据捆绑，达到集合开发的目的
 */
public abstract class BindingAdapter<N> extends BaseAdapter implements Listener{
	protected Context mContext;
	protected List<N> mData;
	private AdapterViewState mViewState;
	private Request mRequest;
	private DataCache mDataCache;
	private int mItemLayoutId;
	//每次读取条数，默认为1
	private int mPerCount;
	protected boolean isRefresh,isMore,isLoading,isSaveCache;

	/**
	 * 数据绑定视图
	 * @param position
	 * @param data
	 * @param holder
	 */
	public abstract void binding(int position,N data,OldViewHolder holder);

	/**
	 * 返回的数据转换
	 * @param result
	 * @return
	 */
	public abstract List<N> translate(Result result);

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

	public BindingAdapter(Context context,Request request,@NonNull int resId){
		this(context,request,resId,false);
	}

	public BindingAdapter(Context context,Request request,@NonNull int resId,boolean saveCache){
		mContext=context;
		mRequest=request;
		isSaveCache=saveCache;
		mPerCount=1;
		mItemLayoutId=resId;
		isRefresh=false;
		isMore=true;
		isLoading=false;
		request.setListener(this);
		mDataCache =new DataCache(request);
		refresh();
	}

	@Override
	public int getCount() {
		return mData==null?0:mData.size();
	}

	@Override
	public N getItem(int position) {
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
		binding(position,mData.get(position),viewHolder);
		return viewHolder.getConvertView();
	}

	private OldViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
		return OldViewHolder.get(mContext, convertView, parent, mItemLayoutId, position);
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
			mDataCache.loadMore(mRequest.getParams());
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
			mDataCache.start();
		else
			NetQueue.getInstance().netRequest(mRequest);
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

	@Override
	public void onResponseListener(Result result){
		List<N> list=translate(result);

		isLoading=false;
		if(list==null||list.size()<=0){
			isMore=false;
			if(mViewState!=null)
				mViewState.onStateChanged(AdapterViewState.STATE_NO_MORE);
			return;
		}
		if(list.size()<mPerCount) {
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
		System.out.println("BindingAdapter error:"+error);
	}
}
