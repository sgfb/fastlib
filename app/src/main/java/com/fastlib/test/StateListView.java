package com.fastlib.test;

import com.fastlib.interf.AdapterViewState;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 具有状态的列表,状态显示在底部
 * 
 * @author sgfb,shenhaofeng
 *
 */
public class StateListView extends ListView implements AdapterViewState{
	private int mCurrState;
	private Map<Integer,LocationView> mViews;
	
	public StateListView(Context context){
		super(context);
	}

	public StateListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mCurrState=-1;
		mViews=new HashMap<>();
	}

	@Override
	public void onStateChanged(int flag) {
		if(mCurrState==AdapterViewState.STATE_NO_MORE)
			return;
		mCurrState=flag;
		LocationView lv=mViews.get(flag);
		if(lv==null||lv.view==null)
			return;
		shwoStateView(mViews.get(flag));
	}

	@Override
	public void addStateView(int state, View view, int location) {
		LocationView lv=new LocationView();
		lv.location=location;
		lv.view=view;
		mViews.put(state,lv);
	}

	private void shwoStateView(LocationView locationView){
		View view=locationView.view;
		switch(locationView.location){
			case AdapterViewState.location_foot:
				removeFootView();
				addFooterView(view,null,false);
				break;
			case AdapterViewState.location_head:
				removeHeadView();
				addHeaderView(view,null,false);
				break;
			case AdapterViewState.location_middle_clear:
				break;
			case AdapterViewState.location_middle_cover:
				break;
			default:
				break;
		}
	}

	private void removeHeadView(){
		Iterator<Integer> iter=mViews.keySet().iterator();

		while(iter.hasNext()){
			int state=iter.next();
			LocationView lv=mViews.get(state);
			if(lv.location==AdapterViewState.location_head)
				removeFooterView(lv.view);
		}
	}
	
	private void removeFootView(){
		Iterator<Integer> iter=mViews.keySet().iterator();

		while(iter.hasNext()){
			int state=iter.next();
			LocationView lv=mViews.get(state);
			if(lv.location==AdapterViewState.location_foot)
			    removeFooterView(lv.view);
		}
	}

	private class LocationView{
		int location;
		View view;
	}
}
