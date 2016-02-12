//package com.fastlib.adapter;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import com.android.volley.MultiPartStringRequest;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response.ErrorListener;
//import com.android.volley.VolleyError;
//import com.android.volley.Request.Method;
//import com.android.volley.Response.Listener;
//import com.android.volley.toolbox.ImageLoader;
//import com.android.volley.toolbox.ImageLoader.ImageCache;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import com.fastlib.base.OldViewHolder;
//import com.fastlib.interf.AdapterViewState;
//import com.library.fastlibrary.R;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.support.annotation.NonNull;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//
///**
// * 绑定适配器，将视图与服务器中的数据捆绑，达到集合开发的目的。
// * 这个类能加快开发速度，不过可能弹性较差
// *
// */
//public abstract class BindingAdapter<N> extends BaseAdapter {
//
//	private Context mContext;
//	private AdapterViewState mViewState;
//	private String mUri;
//	private List<N> mData;
//	private Map<String,Object> mRemoteParams;
//	private RequestQueue mQueue;
//	private StringRequest mStrRequest=null,mDefaultRequest;
//	private MultiPartStringRequest mMultiRequest=null;
//	private int mItemLayoutId;
//	//每次读取条数，默认为1
//	private int mPerCount;
//	private boolean isMore,isLoading;
//
//	private Listener<String> listener=new Listener<String>(){
//
//		@Override
//		public void onResponse(String response) {
//			List<N> t=translate(response);
//			isLoading=false;
//			if(t==null||t.size()<mPerCount){
//				if(mViewState!=null)
//					mViewState.onStateChanged(AdapterViewState.STATE_NO_MORE);
//				if(t==null)
//				    return;
//			}
//			mData.addAll(t);
//			BindingAdapter.this.notifyDataSetChanged();
//			if(mViewState!=null)
//			    mViewState.onStateChanged(AdapterViewState.STATE_LOADED);
//			mRemoteParams=getRemoteParams();
//		}
//	};
//
//	private ErrorListener errorListener=new ErrorListener(){
//		@Override
//		public void onErrorResponse(VolleyError error) {
//			if(mViewState!=null)
//			    mViewState.onStateChanged(AdapterViewState.STATE_ERROR);
//			isLoading=false;
//		}
//	};
//
//	public abstract void binding(int position,N data,OldViewHolder holder);
//	public abstract Map<String,Object> getRemoteParams();
//	public abstract List<N> translate(String response);
//
//	public BindingAdapter(Context context,String uri,@NonNull int resId){
//		mContext=context;
//		mUri=uri;
//		mPerCount=1;
//		mData=new ArrayList<N>();
//		mItemLayoutId=resId;
//		isMore=true;
//		isLoading=false;
//		mQueue=Volley.newRequestQueue(context);
//		mDefaultRequest=new StringRequest(Method.GET,mUri,listener,errorListener);
//		refresh();
//	}
//
//	@Override
//	public int getCount() {
//		return mData==null?0:mData.size();
//	}
//
//	@Override
//	public N getItem(int position) {
//		return mData.get(position);
//	}
//
//	@Override
//	public long getItemId(int position) {
//		return position;
//	}
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		final OldViewHolder viewHolder = getViewHolder(position, convertView, parent);
//		if(position>=getCount()-1&&isMore&&!isLoading)
//			loadRemoteData();
//		binding(position,mData.get(position),viewHolder);
//		return viewHolder.getConvertView();
//	}
//
//	private OldViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
//		return OldViewHolder.get(mContext, convertView, parent, mItemLayoutId, position);
//	}
//
//	/**
//	 * 向服务器请求的参数
//	 */
//	private void loadRemoteData(){
//		isLoading=true;
//		if(mViewState!=null)
//			mViewState.onStateChanged(AdapterViewState.STATE_LOADING);
//		//如果两个请求都为空，使用默认get请求
//		if(mStrRequest==null&&mMultiRequest==null)
//			mQueue.add(mDefaultRequest);
//		else{
//			if(mStrRequest!=null)
//				mQueue.add(mStrRequest);
//			else
//				mQueue.add(mMultiRequest);
//		}
//	}
//
//	/**
//	 * 生成请求.如果是get请求，strRequest和multiRequest都为null。
//	 * 如果是post请求，strRequest和multiReuqest有一个会被实例化
//	 */
//	private void generateRequest(){
//		mRemoteParams=getRemoteParams();
//		boolean strRequest=true;
//		Iterator<String> iter=mRemoteParams.keySet().iterator();
//
//		while(iter.hasNext()){
//			String id=iter.next();
//			Object obj=mRemoteParams.get(id);
//			if(!(obj instanceof String)){
//				strRequest=false;
//				break;
//			}
//		}
//
//		if(strRequest){
//			mStrRequest=new StringRequest(Method.POST,mUri,listener,errorListener){
//				@Override
//				public Map<String,String> getParams(){
//					Map<String,String> map=new HashMap<String,String>();
//					Set<String> keys=mRemoteParams.keySet();
//					Iterator<String> iter=keys.iterator();
//
//					while(iter.hasNext()){
//						String key=iter.next();
//						Object obj=mRemoteParams.get(key);
//						if(obj instanceof String)
//							map.put(key,(String)obj);
//						else if(obj instanceof File)
//							;
//						else
//							throw new IllegalArgumentException("请求网络参数错误");
//					}
//					return map;
//				}
//			};
//			mMultiRequest=null;
//		}
//		else{
//			mMultiRequest=new MultiPartStringRequest(Method.POST,mUri,listener,errorListener){
//				@Override
//				public Map<String,File> getFileUploads(){
//					Map<String,File> params=new HashMap<String,File>();
//					Set<String> keys=mRemoteParams.keySet();
//					Iterator<String> iter=keys.iterator();
//
//					while(iter.hasNext()){
//						String key=iter.next();
//						Object obj=mRemoteParams.get(key);
//						if(obj instanceof File)
//							params.put(key,(File)obj);
//						else if(obj instanceof String)
//							;
//						else
//							throw new IllegalArgumentException("请求网络参数错误");
//					}
//					return params;
//				}
//
//				@Override
//				public Map<String,String> getStringUploads(){
//					Map<String,String> params=new HashMap<String,String>();
//					Set<String> keys=mRemoteParams.keySet();
//					Iterator<String> iter=keys.iterator();
//
//					while(iter.hasNext()){
//						String key=iter.next();
//						Object obj=mRemoteParams.get(key);
//						if(obj instanceof String)
//							params.put(key,(String)obj);
//						else if(obj instanceof File)
//							;
//						else
//							throw new IllegalArgumentException("请求网络参数错误");
//					}
//
//					return params;
//				}
//			};
//			mStrRequest=null;
//		}
//	}
//
//	public void setRemoteParams(Map<String,Object> params){
//		mRemoteParams=params;
//	}
//
//	public void refresh(){
//		generateRequest();
//		mData.clear();
//		loadRemoteData();
//	}
//
//	public void reconnect(){
//		loadRemoteData();
//	}
//
//	public void setViewStateListener(AdapterViewState state){
//		mViewState=state;
//	}
//
//	public void setLoadCount(int count){
//		mPerCount=count;
//	}
//}
