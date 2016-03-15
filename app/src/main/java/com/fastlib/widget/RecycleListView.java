package com.fastlib.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fastlib.interf.AdapterViewState;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * @author sgfb
 *
 * 与listview类似的recycleView.具有下拉刷新，有状态变化
 */
public class RecycleListView extends RelativeLayout implements AdapterViewState{
	private SwipeRefreshLayout mSwipe;
	private RecyclerView mRecyclerView;
	//保存的状态视图，每个状态最多只保存一个视图
	private Map<Integer,View> mStateView;
	private Map<Integer,Integer> mStateLocation;
	//当前状态视图，不同状态不能一起显示
	private View mCurrentStateView;

	public RecycleListView(Context context){
		super(context);
		init();
	}

	public RecycleListView(Context context, AttributeSet attrs) {
		super(context,attrs);
		init();
	}

	/**
	 * 当RecyclerView没有适配器或者适配器返回count小于等于0时，更改状态为empty
	 *
	 * @param state 状态
	 */
	@Override
	public void onStateChanged(int state){
		View v=mStateView.get(state);
		if(mCurrentStateView!=null)
			removeView(mCurrentStateView);
		if(v!=null){
			LayoutParams stateViewLayoutParams=new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			switch (mStateLocation.get(state)){
				//头和尾状态视图暂不可用
				case AdapterViewState.location_head:
					break;
				case AdapterViewState.location_foot:
					break;
				case AdapterViewState.location_middle_clear:
					//这个视图中最多只存在一个状态视图和list视图，所以隐藏了list就等于清除所有视图
					mSwipe.setVisibility(View.INVISIBLE);
					stateViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
					stateViewLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
					break;
				case AdapterViewState.location_middle_cover:
					stateViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
					stateViewLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
					break;
				default:
					break;
			}
			v.setLayoutParams(stateViewLayoutParams);
			addView(v);
		}
	}

	@Override
	public void addStateView(int state, View view, int location){
		mStateLocation.put(state, location);
		mStateView.put(state, view);
	}

	private void init(){
		mSwipe=new SwipeRefreshLayout(getContext());
		mRecyclerView=new RecyclerView(getContext());
		mStateView=new HashMap<>();
		mStateLocation=new HashMap<>();

		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL_LIST));
		mSwipe.addView(mRecyclerView);
		addView(mSwipe);
	}

	public void setAdapter(RecyclerView.Adapter<? extends ViewHolder> adapter){
		mRecyclerView.setAdapter(adapter);
	}

	public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener l){
		mSwipe.setOnRefreshListener(l);
	}
	
	public SwipeRefreshLayout getSwipe(){
		return mSwipe;
	}
	
	public class DividerItemDecoration extends ItemDecoration {
		private final int[] ATTRS=new int[]{
			android.R.attr.listDivider
		};
		
		public static final int HORIZONTAL_LIST=LinearLayoutManager.HORIZONTAL;
		public static final int VERTICAL_LIST=LinearLayoutManager.VERTICAL;
		
		private Drawable mDivider;
		private int mOrientation;
		
		public DividerItemDecoration(Context context,int orientation){
			final TypedArray a=context.obtainStyledAttributes(ATTRS);
			mDivider=a.getDrawable(0);
			a.recycle();
			setOrientation(orientation);
		}
		
		public void setOrientation(int orientation){
			if(orientation!=HORIZONTAL_LIST&&orientation!=VERTICAL_LIST)
				throw new IllegalArgumentException("invalid orientation");
			mOrientation=orientation;
		}
		
		@Override
		public void onDraw(Canvas c,RecyclerView parent){
			if(mOrientation==VERTICAL_LIST)
				drawVertical(c,parent);
			else
				drawHorizontal(c,parent);
		}
		
		public void drawVertical(Canvas c,RecyclerView parent){
			final int left=parent.getPaddingLeft();
			final int right=parent.getWidth()-parent.getPaddingRight();
			
			final int childCount=parent.getChildCount();
			for(int i=0;i<childCount;i++){
				final View child=parent.getChildAt(i);
				RecyclerView.LayoutParams params=(RecyclerView.LayoutParams) child.getLayoutParams();
				final int top=child.getBottom()+params.bottomMargin;
				final int bottom=top+mDivider.getIntrinsicHeight();
				mDivider.setBounds(left, top, right, bottom);
				mDivider.draw(c);
			}
		}
		
		public void drawHorizontal(Canvas c,RecyclerView parent){
			final int top=parent.getPaddingTop();
			final int bottom=parent.getHeight()-parent.getPaddingBottom();
			
			final int childCount=parent.getChildCount();
			for(int i=0;i<childCount;i++){
				final View child=parent.getChildAt(i);
				final RecyclerView.LayoutParams params=(RecyclerView.LayoutParams)child.getLayoutParams();
				final int left=child.getRight()+params.rightMargin;
				final int right=left+mDivider.getIntrinsicWidth();
				mDivider.setBounds(left, top, right, bottom);
				mDivider.draw(c);
			}
		}
		
		@Override
		public void getItemOffsets(Rect outRect,int itemPosition,RecyclerView parent){
			if(mOrientation==VERTICAL_LIST)
				outRect.set(0,0,0,mDivider.getIntrinsicHeight());
			else
				outRect.set(0,0,mDivider.getIntrinsicWidth(),0);
		}
	}
}
