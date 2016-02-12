package com.fastlib.base;

import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.fastlib.R;
import com.fastlib.adapter.BaseListAdapter;
import com.fastlib.bean.Entity;
import com.fastlib.widget.StateListView;

/**
 * 带有ListView的Fragment基类
 * 
 * @author shenhaofeng
 * 
 * @param <T>
 */
public abstract class BaseListFragment<T extends Entity> extends Fragment implements OnRefreshListener, OnScrollListener {

	public final static int STATE_STANDARD = 0;
	public final static int STATE_REFRESHING = 1;
	public final static int STATE_LOADING = 2;

	private BaseListAdapter<T> mAdapter;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private StateListView mListView;
	private int state = STATE_STANDARD;

	private boolean isLastRow = false;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = getListAdapter();
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.base_list_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mListView = (StateListView) view.findViewById(R.id.list);
		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
		mListView.setAdapter(mAdapter);
		mListView.setOnScrollListener(this);
		mSwipeRefreshLayout.setOnRefreshListener(this);
	}

	@Override
	public void onRefresh() {
		requestData(true);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

		if (totalItemCount == visibleItemCount) {
			mAdapter.setState(BaseListAdapter.STATE_LESS_ONR_PAGE);
			return;
		}

		// 判断是否滚到最后一行
		if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
			isLastRow = true;
		} else {
			isLastRow = false;
		}
		if (isLastRow && state == STATE_STANDARD) {// 滚动到最后一行，如果为普通状态,状态变更为加载中,同一时间状态只有一种，所有同一时间这个fragment的请求只有一次
			state = STATE_LOADING;
			requestData(false);
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	/**
	 * requestData请求完成后必须调用该方法后
	 * 
	 * @param data
	 *            新的数据
	 * @param isRefresh
	 *            是否为刷新
	 * @param state
	 *            FooterView的状态
	 */
	public void completeRequest(List<T> data, boolean isRefresh, int state) {
		this.state = STATE_STANDARD;
		if (isRefresh) {
			mAdapter.refreshData(data, state);
			mSwipeRefreshLayout.setRefreshing(false);
		} else {
			mAdapter.addData(data, state);
		}
	}

	/**
	 * 显示加载界面
	 */
	public void showLoadingView() {
		mListView.setState(StateListView.STATE_LOADING);
	}

	/**
	 * 显示无网络界面
	 */
	public void showNoNetworkView() {
		mListView.setState(StateListView.STATE_NO_NETWORK);
	}

	/**
	 * 显示正常列表界面
	 */
	public void showListView() {
		mListView.setState(StateListView.STATE_STANDARD);
	}

	/**
	 * 显示异常界面
	 */
	public void showErrorView() {
		mListView.setState(StateListView.STATE_ERROR);
	}

	/**
	 * 获取适配器，子类必须重写该方法
	 * 
	 * @return
	 */
	protected abstract BaseListAdapter<T> getListAdapter();

	protected abstract void requestData(boolean isRefresh);
}
