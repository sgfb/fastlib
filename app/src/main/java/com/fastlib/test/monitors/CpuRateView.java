package com.fastlib.test.monitors;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.fastlib.R;
import com.fastlib.utils.ScreenUtils;

/**
 * Created by sgfb on 17/1/28.
 */
public class CpuRateView extends View{
    private Paint mPaint;
    private Paint mBgPaint;
    private float[] mPercent;
    private int mCpuCount;

    public CpuRateView(Context context){
        super(context);
        init();
    }

    public CpuRateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        mPaint=new Paint();
        mBgPaint=new Paint();
        mPaint.setColor(Color.parseColor("#7f90CAF9"));
        mBgPaint.setColor(getResources().getColor(R.color.translucent_white));
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(mPercent==null)
            return;
        canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(),mBgPaint);
        for(int i=0;i<mCpuCount;i++) {
            int left=ScreenUtils.getScreenWidth() / mCpuCount * i;
            int right=left+ScreenUtils.getScreenWidth()/mCpuCount;
            canvas.drawRect(left,canvas.getHeight()-(canvas.getHeight()*mPercent[i]/100),right,canvas.getHeight(),mPaint);
        }
    }

    public int getCpuCount() {
        return mCpuCount;
    }

    public void setCpuCount(int cpuCount) {
        mCpuCount = cpuCount;
    }

    public float[] getPercent() {
        return mPercent;
    }

    public void setPercent(float[] percent) {
        mPercent = percent;
        invalidate();
    }
}