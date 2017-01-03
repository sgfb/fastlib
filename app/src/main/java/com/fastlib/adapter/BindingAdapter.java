package com.fastlib.adapter;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import com.fastlib.base.OldViewHolder;
import com.fastlib.base.Refreshable;
import com.fastlib.base.AdapterViewState;
import com.fastlib.net.Listener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 绑定适配器，将视图与服务器中的数据捆绑
 */
public abstract class BindingAdapter<N,R> extends BaseAdapter implements Listener<R> {
	public static final String TAG=BindingAdapter.class.getSimpleName();

	protected Context mContext;
	protected List<N> mData;
	private AdapterViewState mViewState;
	protected Request mRequest;
//	private RemoteCacheServer mRemoteCacheServer;
	private Refreshable mRefreshLayout;
	private ThreadPoolExecutor mThreadPool;
	private int mItemLayoutId;
	private int mPerCount; //每次读取条数，默认为1
	protected boolean isRefresh,isMore,isLoading,isSaveCache;

	public abstract Request generateRequest();

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
	public abstract List<N> translate(R result);

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

	public BindingAdapter(Context context,@LayoutRes int resId){
		this(context,resId,false);
	}

	public BindingAdapter(Context context,@LayoutRes int resId,boolean saveCache){
		mContext=context;
		mRequest= generateRequest();
		isSaveCache=saveCache;
		mPerCount=1;
		mItemLayoutId=resId;
		isRefresh=false;
		isMore=true;
		isLoading=false;
		mRequest.setListener(this);
		mRequest.setGenericName("translate,0");
//		mRemoteCacheServer =new RemoteCacheServer(mRequest);
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
	public View getView(int position, View convertView, ViewGroup parent){
		final OldViewHolder viewHolder = OldViewHolder.get(mContext, convertView, parent, mItemLayoutId);
		if(position>=getCount()-1&&isMore&&!isLoading)
			loadMoreData();
		binding(position,getItem(position),viewHolder);
		return viewHolder.getConvertView();
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
//		if(isSaveCache)
//			mRemoteCacheServer.loadMore(mRequest.getParams());
//		else
		    NetQueue.getInstance().netRequest(mThreadPool,mRequest);
	}

	public void refresh(){
		isLoading=true;
		isRefresh=true;
		//刷新之后也许有更多数据？
		isMore=true;
		getRefreshDataRequest(mRequest);
//		if(isSaveCache)
//			mRemoteCacheServer.start();
//		else
			NetQueue.getInstance().netRequest(mThreadPool,mRequest);
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

	public void setRefreshLayout(Refreshable refreshLayout){
		mRefreshLayout=refreshLayout;
	}

	public ThreadPoolExecutor getThreadPool(){
		return mThreadPool;
	}

	public void setThreadPool(ThreadPoolExecutor threadPool) {
		mThreadPool = threadPool;
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
	public void onResponseListener(Request request,R result){
		if(mRefreshLayout!=null)
			mRefreshLayout.setRefreshStatus(false);
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
	public void onErrorListener(Request r,String error){
		if(mRefreshLayout!=null)
			mRefreshLayout.setRefreshStatus(false);
		isLoading=false;
		System.out.println("BindingAdapter error:"+error);
	}
}
