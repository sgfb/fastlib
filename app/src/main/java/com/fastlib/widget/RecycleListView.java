package com.fastlib.widget;

import java.util.HashMap;
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
import android.widget.FrameLayout;

/**
* 与listview类似的recycleView
 * 具有下拉刷新，侧滑删除item，有状态变化
 * 这个视图实现了分隔符,只需少量的设置就可以使用
 * @author sgfb
 */
public class RecycleListView extends FrameLayout implements AdapterViewState,OnTouchListener{
	private SwipeRefreshLayout mSwipe;
	private RecyclerView mRecyclerView;
	private Map<Integer,View> mStateView;
	private View mCurrentStateView;
	
	public RecycleListView(Context context){
		super(context);
		init();
	}

	public RecycleListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	@Override
	public void onStateChanged(int state){
		View v=mStateView.get(state);
		if(mCurrentStateView!=null)
			removeView(mCurrentStateView);
		if(v!=null)
			addView(v,0);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event){
		return false;
	}
	
	private void init(){
		mSwipe=new SwipeRefreshLayout(getContext());
		mRecyclerView=new RecyclerView(getContext());
		mStateView=new HashMap<Integer,View>();
		
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL_LIST));
		mSwipe.addView(mRecyclerView);
		addView(mSwipe);
	}
	
	public void setAdapter(RecyclerView.Adapter<? extends ViewHolder> adapter){
		mRecyclerView.setAdapter(adapter);
	}
	
	public SwipeRefreshLayout getSwipe(){
		return mSwipe;
	}
	
	/**
	 *设置状态视图,强制全屏
	 * 
	 * @param state
	 * @param v
	 */
	public void addStateView(int state,View v){
		LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		v.setLayoutParams(params);
		mStateView.put(state,v);
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
