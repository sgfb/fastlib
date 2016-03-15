package com.fastlib.test;

import com.fastlib.interf.AdapterViewState;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 * 具有状态的列表,状态显示在底部
 * 
 * @author sgfb,shenhaofeng
 *
 */
public class StateListView extends ListView implements AdapterViewState{
	private int mCurrState;
	private View mNoNetworkView;
	private View mLoadingView;
	private View mNoMoreView;
	private View mErrorView;
	
	public StateListView(Context context){
		this(context,null);
	}

	public StateListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mCurrState=-1;
	}

	@Override
	public void onStateChanged(int flag) {
		if(mCurrState==AdapterViewState.STATE_NO_MORE)
			return;
		switch(flag){
		case AdapterViewState.STATE_LOADING:
			if(mLoadingView==null)
				break;
			removeAllFootView();
			addFooterView(mLoadingView,null,false);
			break;
		case AdapterViewState.STATE_ERROR:
			if(mErrorView==null)
				break;
			removeAllFootView();
			addFooterView(mErrorView,null,false);
			break;
		case AdapterViewState.STATE_NO_MORE:
			if(mNoMoreView==null)
				break;
			removeAllFootView();
			addFooterView(mNoMoreView,null,false);
			mCurrState=AdapterViewState.STATE_NO_MORE;
			break;
		default:;
			break;
		}
	}

	@Override
	public void addStateView(int state, View view, int location) {

	}

	public void setNoNetworkView(View v){
		mNoNetworkView=v;
	}
	
	public void setLoadingView(View v){
		mLoadingView=v;
	}
	
	public void setNoMoreView(View v){
		mNoMoreView=v;
	}
	
	public void setErroView(View v){
		mErrorView=v;
	}
	
	private void removeAllFootView(){
		removeFooterView(mNoMoreView);
		removeFooterView(mErrorView);
		removeFooterView(mNoNetworkView);
		removeFooterView(mLoadingView);
	}
}
