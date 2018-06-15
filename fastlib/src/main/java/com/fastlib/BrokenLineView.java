package com.fastlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.fastlib.utils.DensityUtils;

import java.util.List;

/**
 * Created by Administrator on 2018/6/13.
 */
public class BrokenLineView extends View {
    private final int WIDTH_HORIZONTAL_SPACE =10;
    private final int WIDTH_LINE=3;

    private int mMax=100;
    private List<Integer> mPoints;
    private Paint mLinePaint;

    {
        mLinePaint=new Paint();
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setColor(Color.WHITE);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(WIDTH_LINE);
    }

    public BrokenLineView(Context context) {
        super(context);
    }

    public BrokenLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BrokenLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.BrokenLineView,defStyleAttr,0);
        mLinePaint.setColor(typedArray.getColor(R.styleable.BrokenLineView_color, Color.WHITE));
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mPoints==null||mPoints.isEmpty()||mPoints.size()<1) return;

        for(int i=1;i<mPoints.size();i++){
            int previousY= DensityUtils.dp2px(getContext(),mPoints.get(i-1)*canvas.getHeight()/mMax);
            int valueY= DensityUtils.dp2px(getContext(),mPoints.get(i)*canvas.getHeight()/mMax);
            int offsetZero=i-1;

            canvas.drawLine(offsetZero* WIDTH_HORIZONTAL_SPACE,previousY,offsetZero* WIDTH_HORIZONTAL_SPACE + WIDTH_HORIZONTAL_SPACE, DensityUtils.dp2px(getContext(),valueY),mLinePaint);
        }
    }

    public void setColor(@ColorInt int color){
        mLinePaint.setColor(color);
        invalidate();
    }

    public void setPoints(List<Integer> points){
        mPoints=points;
        ViewGroup.LayoutParams lp= getLayoutParams();
        lp.width=mPoints.size()* WIDTH_HORIZONTAL_SPACE;
        setLayoutParams(lp);
        invalidate();
    }

    public void setMax(int max){
        mMax=max;
        invalidate();
    }
}
