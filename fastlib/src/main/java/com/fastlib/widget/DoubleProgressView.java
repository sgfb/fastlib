package com.fastlib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by sgfb on 2018/6/12.
 */
public class DoubleProgressView extends View{
    private final int RADIUS=15;

    private boolean isSelectLeft; //or right
    private int mMin=0;
    private int mMax=100;
    private float mLeftCirclePosition=RADIUS/2,mRightCirclePosition=100;
    private Paint mCirclePaint;
    private Paint mProgressPaint;
    private Paint mBackgroundLinePaint;
    private OnProgressChanged mListener;
    private int mCurrLeftValue=-1;
    private int mCurrRightValue=-1;

    {
        mBackgroundLinePaint=new Paint();
        mBackgroundLinePaint.setColor(Color.WHITE);
        mBackgroundLinePaint.setStyle(Paint.Style.FILL);

        mCirclePaint=new Paint();
        mCirclePaint.setColor(Color.WHITE);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAntiAlias(true);

        mProgressPaint=new Paint();
        mProgressPaint.setColor(Color.GREEN);
        mProgressPaint.setStyle(Paint.Style.FILL);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event){
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        isSelectLeft=Math.abs(mLeftCirclePosition-event.getX())<Math.abs(mRightCirclePosition-event.getX());
                        translationCircle(event.getX());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        translationCircle(event.getX());
                        break;
                }
                return true;
            }
        });
    }

    private void translationCircle(float newX){
        if(isSelectLeft) {
            mLeftCirclePosition=newX;
            if(mLeftCirclePosition>mRightCirclePosition) mLeftCirclePosition=mRightCirclePosition;
        }
        else {
            mRightCirclePosition=newX;
            if(mRightCirclePosition<mLeftCirclePosition) mRightCirclePosition=mLeftCirclePosition;
        }
        invalidate();
    }

    public DoubleProgressView(Context context) {
        super(context);
    }

    public DoubleProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DoubleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMax(int max){
        if(mMax<mMin)
            mMax=mMin+1;
        mMax=max;
        invalidate();
    }

    public void setMin(int min){
        if(mMin>mMax)
            mMax=mMin+1;
        mMin=min;
        invalidate();
    }

    public void setCurrLeftValue(int value){
        mCurrLeftValue=value;
        if(mCurrLeftValue<mMin)
            mCurrLeftValue=mMin;
        invalidate();
    }

    public void setCurrRightValue(int value){
        mCurrRightValue=value;
        if(mCurrRightValue>mMax)
            mCurrRightValue=mMax;
        invalidate();
    }

    public void setOnProgressChangedListener(OnProgressChanged listener){
        mListener =listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mCurrLeftValue!=-1)
            mLeftCirclePosition=mCurrLeftValue*canvas.getWidth()/(mMax-mMin);
        if(mCurrRightValue!=-1)
            mRightCirclePosition=mCurrRightValue*canvas.getWidth()/(mMax-mMin);
        float leftCirclePosition=mLeftCirclePosition;
        float rightCirclePosition=mRightCirclePosition;

        if(leftCirclePosition<RADIUS) leftCirclePosition=RADIUS;
        else if(leftCirclePosition>canvas.getWidth()-RADIUS) leftCirclePosition=canvas.getWidth()-RADIUS;
        if(rightCirclePosition<RADIUS) rightCirclePosition=RADIUS;
        else if(rightCirclePosition>canvas.getWidth()-RADIUS) rightCirclePosition=canvas.getWidth()-RADIUS;

        canvas.drawRect(0,canvas.getHeight()/2-2,canvas.getWidth(),canvas.getHeight()/2+2,mBackgroundLinePaint);
        canvas.drawRect(leftCirclePosition,canvas.getHeight()/2-2,rightCirclePosition,canvas.getHeight()/2+2,mProgressPaint);
        canvas.drawCircle(leftCirclePosition,canvas.getHeight()/2,RADIUS,mCirclePaint);
        canvas.drawCircle(rightCirclePosition,canvas.getHeight()/2,RADIUS,mCirclePaint);

        float rightValue=mRightCirclePosition/canvas.getWidth();
        if(rightValue>0.99) rightValue=1;

        float leftValue=mLeftCirclePosition/canvas.getWidth();
        if(leftValue<0) leftValue=0;
        if(mListener!=null)
            mListener.progressChanged(mCurrLeftValue!=-1||mCurrRightValue!=-1,mMin+(int)(leftValue*(mMax-mMin)),mMin+(int)(rightValue*(mMax-mMin)));
        mCurrLeftValue=-1;
        mCurrRightValue=-1;
    }

    public interface OnProgressChanged{
        void progressChanged(boolean fromUser, int leftValue, int rightValue);
    }
}