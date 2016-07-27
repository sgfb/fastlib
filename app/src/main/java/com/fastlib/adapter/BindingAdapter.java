package com.fastlib.adapter;

import java.lang.reflect.Type;
import java.util.List;

import com.fastlib.base.OldViewHolder;
import com.fastlib.db.RemoteCache;
import com.fastlib.interf.AdapterViewState;
import com.fastlib.net.Listener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;
import com.fastlib.net.Result;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 绑定适配器，将视图与服务器中的数据捆绑
 */
public abstract class BindingAdapter<N> extends BaseAdapter implements Listener{
	public static final String TAG=BindingAdapter.class.getSimpleName();

	protected Context mContext;
	protected List<N> mData;
	private AdapterViewState mViewState;
	private Request mRequest;
	private RemoteCache mRemoteCache;
	private int mItemLayoutId;
	//每次读取条数，默认为1
	private int mPerCount;
	protected boolean isRefresh,isMore,isLoading,isSaveCache;

	public abstract Request getRequest();

	/**
	 * 数据绑定视图
	 * @param position
	 * @param data
	 * @param holder
	 */
	public abstract void binding(int position,N data,OldViewHolder holder);

	/**
	 * 返回的数据转换.通常情况下都需要覆写这个方法
	 * @param result
	 * @return
	 */
	public List<N> translate(Result result){
		if(result.isSuccess()){
			try{
				Gson gson=new Gson();
				Type type=new TypeToken<List<N>>(){}.getType();
				return gson.fromJson(result.getBody(),type);
			}catch(JsonParseException e){
				Log.d(TAG,e.toString());
			}
		}
		return null;
	}

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

	public BindingAdapter(Context context,@NonNull int resId){
		this(context, resId, false);
	}

	public BindingAdapter(Context context,@NonNull int resId,boolean saveCache){
		mContext=context;
		mRequest=getRequest();
		isSaveCache=saveCache;
		mPerCount=1;
		mItemLayoutId=resId;
		isRefresh=false;
		isMore=true;
		isLoading=false;
		mRequest.setListener(this);
		mRemoteCache =new RemoteCache(mRequest);
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
		final OldViewHolder viewHolder = getViewHolder(convertView, parent);
		if(position>=getCount()-1&&isMore&&!isLoading)
			loadMoreData();
		binding(position,getItem(position),viewHolder);
		return viewHolder.getConvertView();
	}

	private OldViewHolder getViewHolder(View convertView, ViewGroup parent) {
		return OldViewHolder.get(mContext, convertView, parent, mItemLayoutId);
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
			mRemoteCache.loadMore(mRequest.getParams());
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
			mRemoteCache.start();
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

	/**
	 * 设置是否仅请求一次,需要在请求之前设置
	 * @param requestOnce
	 */
	public void setRequestOnce(boolean requestOnce){
		isMore=!requestOnce;
	}

	/**
	 * 如何对待新数据
	 * @param list
	 */
	public void dataRefresh(List<N> list){
		if(isRefresh)
			mData = list;
		else
			mData.addAll(list);
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
		if(list.size()<mPerCount){
			isMore = false;
			if(mViewState!=null)
				mViewState.onStateChanged(AdapterViewState.STATE_NO_MORE);
		}
		dataRefresh(list);
		notifyDataSetChanged();
	}

	@Override
	public void onErrorListener(String error) {
		System.out.println("BindingAdapter error:"+error);
	}
}
