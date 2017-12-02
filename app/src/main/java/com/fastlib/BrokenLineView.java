package com.fastlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.fastlib.utils.DensityUtils;

/**
 * Created by sgfb on 2017/11/8.
 * 折线图(临时用)
 */
public class BrokenLineView extends View{
    private int mMaxValue=10;
    private Paint mLinePaint;
    private Paint mBg1Paint;
    private Paint mBg2Paint;
    private Paint mDividerPaint;
    private Paint mTextPaint;
    private int[] mValues=new int[]{1,2,3,4,5,6,7};
    private String[] mVerticalTitle=new String[]{"X"," 10","","  5","","  0"};
    private String[] mHorizontalTitle=new String[]{"1","2","3","4","5","6","7"};

    public BrokenLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        mLinePaint=new Paint();
        mBg1Paint=new Paint();
        mBg2Paint=new Paint();
        mDividerPaint=new Paint();
        mTextPaint=new Paint();

        mLinePaint.setColor(Color.parseColor("#FF9602"));
        mBg1Paint.setColor(Color.parseColor("#F2F2F2"));
        mBg2Paint.setColor(Color.WHITE);
        mDividerPaint.setColor(Color.parseColor("#ECECEC"));
        mTextPaint.setColor(Color.parseColor("#999999"));
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(DensityUtils.dp2px(getContext(),2));
        mTextPaint.setTextSize(DensityUtils.sp2px(getContext(),13));
        mTextPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int textSize= (int) mTextPaint.getTextSize();
        int width=canvas.getWidth()-textSize*6;
        int height=canvas.getHeight()-textSize*6;
        int blockWidth=width/6;
        int blockHeight=height/5;
        int offset=textSize*3;
        int rectBottom=height+offset;

        for(int i=0;i<3;i++){
            int top=blockHeight*i*2+offset;
            canvas.drawRect(offset,top,width+offset,top+blockHeight,mBg1Paint);
        }
        for(int i=0;i<2;i++){
            int top=blockHeight*i*2+blockHeight+offset;
            canvas.drawRect(offset,top,width+offset,top+blockHeight,mBg2Paint);
        }
        for(int i=0;i<6;i++){
            int top=blockHeight*i+offset;
            canvas.drawText(mVerticalTitle[i],textSize,top+textSize/2,mTextPaint);
        }
        for(int i=0;i<7;i++){
            int x=blockWidth*i+offset;
            int circleY=height-(height*mValues[i]/ mMaxValue)+offset;

            canvas.drawText(mHorizontalTitle[i],x-textSize/2,rectBottom+(int)(textSize*1.5),mTextPaint);
            canvas.drawLine(x,offset,x,rectBottom,mDividerPaint);
            canvas.drawCircle(x,circleY,DensityUtils.dp2px(getContext(),4),mLinePaint);
            if(i<mValues.length-1){
                int nextX=x+blockWidth;
                int nextCircleY=height-(height*mValues[i+1]/ mMaxValue)+offset;
                canvas.drawLine(x,circleY,nextX,nextCircleY,mLinePaint);
            }
        }
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public void setMaxValue(int maxValue) {
        mMaxValue = maxValue;
    }

    public Paint getLinePaint() {
        return mLinePaint;
    }

    public Paint getBg1Paint() {
        return mBg1Paint;
    }

    public Paint getBg2Paint() {
        return mBg2Paint;
    }

    public Paint getDividerPaint() {
        return mDividerPaint;
    }

    public Paint getTextPaint() {
        return mTextPaint;
    }

    public int[] getValues() {
        return mValues;
    }

    public void setValues(int[] values) {
        mValues = values;
    }

    public String[] getVerticalTitle() {
        return mVerticalTitle;
    }

    public void setVerticalTitle(String[] verticalTitle) {
        mVerticalTitle = verticalTitle;
    }

    public String[] getHorizontalTitle() {
        return mHorizontalTitle;
    }

    public void setHorizontalTitle(String[] horizontalTitle) {
        mHorizontalTitle = horizontalTitle;
    }
}