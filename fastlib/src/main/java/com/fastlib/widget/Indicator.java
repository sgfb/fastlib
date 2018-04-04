package com.fastlib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 适用于轮播底部圆点 特点：
 * 1.默认除了mIndex的圆点其他圆点都是半透明
 * 2.点居中,两边没有额外的间隙与圆间距相等
 */
public class Indicator extends View{
	private final int BASE_CIRCLE_SIZE=4; //基础大小 dp单位

	//总共页数（圆点数）
	private int mCount=0;
	private int mIndex=0;
	private float mCircleRadius=BASE_CIRCLE_SIZE;
	private Paint mSelectPaint;
	private Paint mUnSelectPaint;
	
	public Indicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public Indicator(Context context, int pageCount, int index){
		super(context);
		mIndex=index;
		mCount =pageCount;
		init();
	}

	private void init(){
		mSelectPaint =new Paint();
		mUnSelectPaint=new Paint();
		//灰色，抗锯齿
		mSelectPaint.setColor(Color.GRAY);
		mSelectPaint.setAntiAlias(true);
		mUnSelectPaint.setColor(Color.GRAY);
		mUnSelectPaint.setAntiAlias(true);
		mUnSelectPaint.setAlpha(120);
	}
	
	@Override
	public void onDraw(Canvas canvas){
		int width=canvas.getWidth();
		int height=canvas.getHeight();
		float circleSpacing=width/(mCount+1); //圆心间隔
		Paint currPaint;

		for(int i=0;i<mCount;i++){
			currPaint=(i==mIndex)?mSelectPaint:mUnSelectPaint;
			canvas.drawCircle(circleSpacing*(i+1),height/2,mCircleRadius,currPaint);
		}
	}
	
	public void setCurrentItem(int index){
		mIndex=index;
		invalidate();
	}
	
	public void setItemCount(int count){
		mCount =count;
		invalidate();
	}

	public void setSelectColor(Paint paint){
		mSelectPaint=paint;
	}

	public void setUnSelectColor(Paint paint){
		mUnSelectPaint=paint;
	}

	public void setSelectColor(int color){
		mSelectPaint.setColor(color);
	}

	public void setUnSelectColor(int color){
		mUnSelectPaint.setColor(color);
	}

	public void setCircleSize(float circleSize){
		mCircleRadius=circleSize;
		invalidate();
	}

	public float getCircleSize(){
		return mCircleRadius;
	}
}