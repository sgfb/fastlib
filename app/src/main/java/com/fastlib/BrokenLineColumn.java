package com.fastlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by sgfb on 2017/10/13.
 * 自定义折线图单元.一个单元一列
 */
public class BrokenLineColumn extends View{
    public boolean isDrawFullLeftCircle=false,isDrawFullRightCircle=false;
    private int mSemiCircleSize=15; //半圆大小
    private int mRowCount=2; //行数
    private float mMax=1; //最大数
    private float mStartValue=0,mEndVale=0; //起始数和终点数
    private Paint mBgPaint;
    private Paint mBgPaint2;
    private Paint mSemiCirclePaint;
    private Paint mBrokenLinePaint;
    private Paint mDividerPaint;
    private RectF mRect;

    public BrokenLineColumn(Context context) {
        super(context);
        init();
    }

    public BrokenLineColumn(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        mRect=new RectF();
        mBgPaint =new Paint();
        mBgPaint2=new Paint();
        mSemiCirclePaint=new Paint();
        mBrokenLinePaint=new Paint();
        mDividerPaint=new Paint();

        mBgPaint.setColor(Color.GRAY);
        mBgPaint2.setColor(Color.WHITE);
        mSemiCirclePaint.setAntiAlias(true);
        mSemiCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBrokenLinePaint.setAntiAlias(true);
        mBrokenLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBrokenLinePaint.setStrokeWidth(2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //计算出每个背景框高度，折线起始和终止高度位置
        int cellHeight=canvas.getHeight()/mRowCount;
        float lineStartPosition=(1-mStartValue/mMax)*canvas.getHeight();
        float lineEndPosition=(1-mEndVale/mMax)*canvas.getHeight();

        //根据奇偶画出两种颜色背景
        for(int i=0;i<mRowCount;i++)
            canvas.drawRect(0,i*cellHeight,canvas.getWidth(),canvas.getHeight(),i%2==1?mBgPaint:mBgPaint2);
        //画间隔线
        canvas.drawLine(mSemiCircleSize/2,0,mSemiCircleSize/2,canvas.getHeight(),mDividerPaint);
        //画折线
        canvas.drawLine(0,lineStartPosition,canvas.getWidth(),lineEndPosition,mSemiCirclePaint);
        //画左圆;
        canvas.drawCircle(mSemiCircleSize/2,lineStartPosition-mSemiCircleSize/2,mSemiCircleSize/2,mSemiCirclePaint);
//        mRect.set(-mSemiCircleSize/2,lineStartPosition-mSemiCircleSize/2,mSemiCircleSize/2,lineStartPosition+mSemiCircleSize/2);
//        if(isDrawFullLeftCircle) canvas.drawCircle(mSemiCircleSize/2,lineStartPosition-mSemiCircleSize/2,mSemiCircleSize/2,mSemiCirclePaint);
//        else canvas.drawArc(mRect,-90,180,true, mSemiCirclePaint);
        //画右圆
//        mRect.set(canvas.getWidth()-mSemiCircleSize/2,lineEndPosition-mSemiCircleSize/2,canvas.getWidth()+mSemiCircleSize/2,lineEndPosition+mSemiCircleSize/2);
//        if(isDrawFullRightCircle) canvas.drawCircle(canvas.getWidth()-mSemiCircleSize/2,lineEndPosition-mSemiCircleSize/2,mSemiCircleSize/2,mSemiCirclePaint);
//        else canvas.drawArc(mRect,90,180,true,mSemiCirclePaint);
    }

    public int getmRowCount() {
        return mRowCount;
    }

    public void setmRowCount(int mRowCount) {
        this.mRowCount = mRowCount;
    }

    public float getmMax() {
        return mMax;
    }

    public void setmMax(int mMax) {
        this.mMax = mMax;
    }

    public float getmStartValue() {
        return mStartValue;
    }

    public void setmStartValue(int mStartValue) {
        this.mStartValue = mStartValue;
        invalidate();
    }

    public float getmEndVale() {
        return mEndVale;
    }

    public void setmEndVale(int mEndVale) {
        this.mEndVale = mEndVale;
    }

    public void setmBrokenLineColor(int mBrokenLineColor) {
        mBrokenLinePaint.setColor(mBrokenLineColor);
        invalidate();
    }


    public void setmSemiCircleColor(int mSemiCircleColor) {
        mSemiCirclePaint.setColor(mSemiCircleColor);
    }

    public int getmSemiCircleSize() {
        return mSemiCircleSize;
    }

    public void setmSemiCircleSize(int mSemiCircleSize) {
        this.mSemiCircleSize = mSemiCircleSize;
    }
}