package com.fastlib.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fastlib.R;
import com.fastlib.bean.StateLocationView;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * @author sgfb
 *
 * 与listview类似的recycleView.具有下拉刷新，有状态变化
 */
public class RecycleListView extends RelativeLayout implements AdapterViewState{
	private SwipeRefreshLayout mSwipe;
	private RecyclerView mRecyclerView;
	private LinearLayoutManager mLayoutManager;
	private DividerItemDecoration mDividerItemDecoration;
	private Map<Integer,StateLocationView> mStateView;
	private LinearLayout mHeadView;
	private LinearLayout mFootView;
	private boolean mAutofit;

	public RecycleListView(Context context){
		super(context);
		init();
	}

	public RecycleListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init(){
		mSwipe=new SwipeRefreshLayout(getContext());
		mRecyclerView=new RecyclerView(getContext());
		mHeadView=new LinearLayout(getContext());
		mFootView=new LinearLayout(getContext());
		mStateView=new HashMap<>();

		mRecyclerView.setLayoutManager(mLayoutManager = new LinearLayoutManager(getContext()));
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.addItemDecoration(mDividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
		mSwipe.addView(mRecyclerView);
		mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (mAutofit && newState == RecyclerView.SCROLL_STATE_IDLE) {
					if (mDividerItemDecoration.getOrientation() == LinearLayoutManager.HORIZONTAL) {
						int left = recyclerView.getChildAt(0).getLeft();
						int right = recyclerView.getChildAt(0).getRight();
						if (left < 0 && right > 1)
							mRecyclerView.smoothScrollBy(right - 1, 0);
					} else {
						int top = recyclerView.getChildAt(0).getTop();
						int bottom = recyclerView.getChildAt(0).getBottom();
						if (top < 0 && bottom > 1)
							mRecyclerView.smoothScrollBy(0, bottom - 1);
					}
				}
			}
		});
		mSwipe.setId(R.id.swipe);
		mHeadView.setId(R.id.headView);
		mFootView.setId(R.id.bottomView);
		LayoutParams swipeLp=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		LayoutParams footLp=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		swipeLp.addRule(RelativeLayout.ABOVE,mFootView.getId());
		swipeLp.addRule(RelativeLayout.BELOW, mHeadView.getId());
		footLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		mFootView.setLayoutParams(footLp);
		mSwipe.setLayoutParams(swipeLp);
		addView(mHeadView);
		addView(mFootView);
		addView(mSwipe);
	}

	/**
	 * 当RecyclerView没有适配器或者适配器返回count小于等于0时，更改状态为empty
	 *
	 * @param state 状态
	 */
	@Override
	public void onStateChanged(int state){
		StateLocationView slv=mStateView.get(state);
		if(slv==null)
			return;
		changedState(slv.location, slv.view);
	}

	@Override
	public void addStateView(int state, View view, int location){
		StateLocationView slv=new StateLocationView();
		slv.location=location;
		slv.view=view;
		mStateView.put(state, slv);
	}

	private void changedState(int location,View view){
		mSwipe.setVisibility(View.VISIBLE);
		switch (location){
			case AdapterViewState.LOCATION_HEAD:
				if(mHeadView.getChildCount()>0)
					mHeadView.removeViewAt(0);
				mHeadView.addView(view);
				break;
			case AdapterViewState.LOCATION_FOOT:
				if(mFootView.getChildCount()>0)
					mFootView.removeViewAt(0);
				mFootView.addView(view);
				break;
			case AdapterViewState.LOCATION_MIDDLE_CLEAR:
				if(mHeadView.getChildCount()>0)
					mHeadView.removeViewAt(0);
				if(mFootView.getChildCount()>0)
					mFootView.removeViewAt(0);
				mSwipe.setVisibility(View.GONE);
				mHeadView.addView(view);
				break;
			case AdapterViewState.LOCATION_MIDDLE_COVER:
				//不知道怎么做
				break;
			default:
				break;
		}
	}

	public void setOrientation(int orientation){
		mLayoutManager.setOrientation(orientation);
		mDividerItemDecoration.setOrientation(orientation);
	}

	/**
	 * 首个item贴住start位置
	 * @param autofit
	 */
	public void enableAutofit(boolean autofit){
		mAutofit=autofit;
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

	public RecyclerView getRecyclerView(){
		return mRecyclerView;
	}

	public ItemDecoration getDecoration(){
		return mDividerItemDecoration;
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

		public int getOrientation(){
			return mOrientation;
		}

		public int getDividerHeight(){
			return mDivider.getIntrinsicHeight();
		}

		public int getDividerWidth(){
			return mDivider.getIntrinsicWidth();
		}
	}
}
