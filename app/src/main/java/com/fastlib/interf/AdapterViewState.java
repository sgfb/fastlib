package com.fastlib.interf;

import android.view.View;

/**
 * 实现这个接口的视图就代表这个视图是具有状态的
 */
public interface AdapterViewState{
	int STATE_EMPTY=1;
	int STATE_LOADING=2;
	int STATE_LOADED=3;
	int STATE_NO_MORE=4;
	int STATE_ERROR=5;
	int STATE_NO_NETWORK=6;

	int location_head=1;
	int location_middle_cover=2;
	int location_middle_clear=3;
	int location_foot=4;

	/**
	 * 更改状态
	 * @param state 状态
	 */
	void onStateChanged(int state);

	/**
	 * 增加状态视图
	 * @param state 状态
	 * @param view 视图
	 * @param location 何种形式生成位置
	 */
	void addStateView(int state,View view,int location);
}
