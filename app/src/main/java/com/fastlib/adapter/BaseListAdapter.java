package com.fastlib.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fastlib.R;
import com.fastlib.base.OldViewHolder;
import com.fastlib.bean.Entity;

/**
 * ListView的基础适配器
 * 
 * @author shenhaofeng
 * 
 */
public abstract class BaseListAdapter<T extends Entity> extends BaseAdapter {

	public final static int ITEM_EMPYY = -1;
	public final static int ITEM_FOOTER = -2;

	/**
	 * 加载中
	 */
	public final static int STATE_LOADING = 0;
	public final static int STATE_EMPTY = 1;
	public final static int STATE_NO_MORE = 2;
	public final static int STATE_NO_NETWORK = 3;
	public final static int STATE_LESS_ONR_PAGE = 4;

	private int state = STATE_LESS_ONR_PAGE;
	protected int mItemLayoutId;

	/**
	 * 加载页脚
	 */
	private View mFooterView;

	public List<T> mData = new ArrayList<T>();

	public BaseListAdapter(int layoutRes) {
		mItemLayoutId=layoutRes;
	}

	public BaseListAdapter(int layoutRes,List<T> data) {
		mItemLayoutId=layoutRes;
		mData.addAll(data);
	}

	@Override
	public int getCount() {
		return mData.size() + 1;// 这里+1是给FootView留出位置
	}

	@Override
	public Object getItem(int arg0) {
		return mData.get(arg0);

	}

	@Override
	public long getItemId(int arg0) {
		if (getCount() == 1) {// 数据为空时
			return ITEM_EMPYY;
		} else {
			if (arg0 == getCount() - 1) {// 数据不为空，且到了footerView
				return ITEM_FOOTER;
			} else {
				return arg0;
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 判断页面页脚的显示状态
		if (position == getCount() - 1) {
			if (mFooterView == null) {
				mFooterView = LayoutInflater.from(parent.getContext()).inflate(R.layout.base_loading_foot, parent, false);
			}
			ProgressBar loadingView = (ProgressBar) mFooterView.findViewById(R.id.base_foot_loading);
			TextView loadingTv = (TextView) mFooterView.findViewById(R.id.base_foot_text);
			switch (state) {
			case STATE_LOADING:
				loadingView.setVisibility(View.VISIBLE);
				loadingTv.setVisibility(View.VISIBLE);
				loadingTv.setText("加载中");
				break;
			case STATE_NO_MORE:
				loadingView.setVisibility(View.GONE);
				loadingTv.setVisibility(View.VISIBLE);
				loadingTv.setText("没有更多");
				break;
			case STATE_NO_NETWORK:
				loadingView.setVisibility(View.GONE);
				loadingTv.setVisibility(View.VISIBLE);
				loadingTv.setText("没有网络");
				break;
			case STATE_EMPTY:
				loadingView.setVisibility(View.GONE);
				loadingTv.setVisibility(View.VISIBLE);
				loadingTv.setText("没有数据");
				break;
			case STATE_LESS_ONR_PAGE:
				loadingView.setVisibility(View.GONE);
				loadingTv.setVisibility(View.GONE);
				loadingTv.setText("");
				break;
			default:
				break;
			}
			return mFooterView;
		}

		final OldViewHolder viewHolder = getViewHolder(position, convertView, parent);
		convert(viewHolder, (T)getItem(position));
		return viewHolder.getConvertView();

//		return getRealView(position, convertView, parent);
	}

	/**
	 * 设置页脚状态
	 * 
	 * @param state
	 */
	public void setState(int state) {
		this.state = state;
	}

	/**
	 * 数据更新，并且刷新页脚状态
	 * 
	 * @param state
	 */
	public void notifyDataSetChanged(int state) {
		setState(state);
		notifyDataSetChanged();
	}

	/**
	 * 添加一条数据
	 * 
	 * @param data
	 */
	public void addData(T data, int state) {
		mData.add(data);
		notifyDataSetChanged(state);
	}

	/**
	 * 添加一组数据
	 * 
	 * @param data
	 */
	public void addData(List<T> data, int state) {
		mData.addAll(data);
		notifyDataSetChanged(state);
	}

	/**
	 * 移除指定位置的数据
	 * 
	 * @param index
	 */
	public void removeData(int index, int state) {
		mData.remove(index);
		notifyDataSetChanged(state);
	}

	/**
	 * 移除指定对象
	 * 
	 * @param data
	 */
	public void removeData(T data, int state) {
		mData.remove(data);
		notifyDataSetChanged(state);
	}

	/**
	 * 清除数据
	 */
	public void clearData() {
		mData.clear();
		notifyDataSetChanged();
	}

	/**
	 * 刷新数据
	 * 
	 * @param data
	 */
	public void refreshData(List<T> data, int state) {
		mData.clear();
		mData.addAll(data);
		notifyDataSetChanged(state);
	}

	private OldViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
		return OldViewHolder.get(parent.getContext(), convertView, parent, mItemLayoutId, position);
	}

	/**
	 * 需要实现的抽象方法，这里进行数据源和空间的绑定
	 * 
	 * @param holder
	 *            View持有对象，使用getView(R.id.demo)的格式取出View
	 * @param item
	 *            数据源
	 */
	public abstract void convert(OldViewHolder holder, T item);

	/**
	 * 这里适配数据与View
	 * 
	 * @return
	 */
//	public abstract View getRealView(int pos, View cacheView, ViewGroup parentView);

}
