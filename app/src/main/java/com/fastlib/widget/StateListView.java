package com.fastlib.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fastlib.R;

/**
 * 有状态页的ListView,异常界面能够单击重新刷新
 * 
 * @author shenhaofeng
 * 
 */
public class StateListView extends ListView implements View.OnClickListener {

	public final static int STATE_STANDARD = 0;
	public final static int STATE_LOADING = 1;
	public final static int STATE_NO_NETWORK = 2;
	public final static int STATE_ERROR = 3;

	private LinearLayout mStateLayout;
	private TextView mStateStr;
	private ImageView mStateImage;
	private ProgressBar mStateLoading;

	private Bitmap mNoNetworkImg;
	private Bitmap mErrorImg;

	private int state = STATE_STANDARD;

	private onRefreshListener mListener;

	public StateListView(Context context) {
		super(context);
	}

	public StateListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StateListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setState(int state) {
		this.state = state;
		switch (this.state) {
		case STATE_STANDARD:
			this.setVisibility(View.VISIBLE);
			mStateStr.setVisibility(View.GONE);
			mStateImage.setVisibility(View.GONE);
			mStateLoading.setVisibility(View.GONE);
			break;
		case STATE_LOADING:
			this.setVisibility(View.GONE);
			mStateStr.setVisibility(View.VISIBLE);
			mStateImage.setVisibility(View.GONE);
			mStateLoading.setVisibility(View.VISIBLE);
			mStateStr.setText("加载中");
			break;
		case STATE_NO_NETWORK:
			this.setVisibility(View.GONE);
			mStateStr.setVisibility(View.VISIBLE);
			mStateImage.setVisibility(View.VISIBLE);
			mStateLoading.setVisibility(View.GONE);
			mStateImage.setImageResource(R.drawable.error_default);
			mStateStr.setText("没有网络");
			if (mNoNetworkImg != null) {
				mStateImage.setImageBitmap(mNoNetworkImg);
			}
			break;
		case STATE_ERROR:
			this.setVisibility(View.GONE);
			mStateStr.setVisibility(View.VISIBLE);
			mStateImage.setVisibility(View.VISIBLE);
			mStateLoading.setVisibility(View.GONE);
			mStateImage.setImageResource(R.drawable.error_default);
			mStateStr.setText("异常");
			if (mErrorImg != null) {
				mStateImage.setImageBitmap(mErrorImg);
			}
			break;
		default:
			break;
		}

	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		initStateLayout();

	}

	/**
	 * 初始化
	 */
	private void initStateLayout() {
		mStateLayout = new LinearLayout(getContext());
		mStateLayout.setGravity(Gravity.CENTER);
		mStateLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mStateLayout.setOrientation(LinearLayout.VERTICAL);
		mStateStr = new TextView(getContext());
		mStateStr.setGravity(Gravity.CENTER);
		mStateImage = new ImageView(getContext());
		mStateLoading = new ProgressBar(getContext());
		mStateLayout.addView(mStateImage);
		mStateLayout.addView(mStateLoading);
		mStateLayout.addView(mStateStr);
		mStateLayout.setOnClickListener(this);
		ViewGroup vg = (ViewGroup) getParent();
		if (vg != null) {
			vg.addView(mStateLayout);
		}
	}

	@Override
	public void onClick(View arg0) {
		if (mListener != null&&state!=STATE_LOADING) {
			setState(STATE_LOADING);
			mListener.onRefresh();
		}
	}

	public void setOnRefreshListener(onRefreshListener listener) {
		this.mListener = listener;
	}

	public void setNoNetworkBitmap(Bitmap bitmap) {
		mNoNetworkImg = bitmap;
	}

	public void setErrorBitmap(Bitmap bitmap) {
		mErrorImg = bitmap;
	}

	/**
	 * StateListView 在重新刷新后被触发
	 * 
	 * @author shenhaofeng
	 * 
	 */
	public interface onRefreshListener {

		public void onRefresh();
	}

}
