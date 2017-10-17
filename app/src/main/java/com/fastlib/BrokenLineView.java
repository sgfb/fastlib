package com.fastlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.fastlib.utils.DensityUtils;

import java.util.Random;

/**
 * Created by sgfb on 2017/10/14.
 * 折线图
 */
public class BrokenLineView extends View{
    private int mColumns=7;
    private int mRows=6;
    private Paint mBgPaint;
    private Paint mBgPaint2;
    private Paint mBrokenLinePaint;
    private Paint mCirclePaint;
    private Paint mDividerPaint;
    private RectF mRect=new RectF();
    private Random mRandom=new Random();

    public BrokenLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        mBgPaint=new Paint();
        mBgPaint2=new Paint();
        mBrokenLinePaint=new Paint();
        mCirclePaint=new Paint();
        mDividerPaint=new Paint();

        mBgPaint.setColor(Color.WHITE);
        mBgPaint2.setColor(Color.GRAY);
        mBrokenLinePaint.setColor(Color.YELLOW);
        mCirclePaint.setColor(Color.YELLOW);
        mCirclePaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas){
        //上下左右都留出10dp边距
        final int bgPadding=DensityUtils.dp2px(getContext(),10);
        int cellHeight=(canvas.getHeight()-bgPadding*2)/mRows;
        int cellWidth=(canvas.getWidth()-bgPadding*2)/mColumns;

        //画背景
        for(int i=0;i<mRows;i++)
            canvas.drawRect(bgPadding,i*cellHeight+bgPadding
                    ,canvas.getWidth()-bgPadding,i*cellHeight+cellHeight+bgPadding,i%2==0?mBgPaint:mBgPaint2);
        for(int i=0;i<mColumns;i++){
            float random=mRandom.nextFloat();
            int dividerX=i*cellWidth+bgPadding;
            //画间隔线
            canvas.drawLine(dividerX,bgPadding,dividerX,canvas.getHeight()-bgPadding,mDividerPaint);
            //画圆
            canvas.drawCircle(dividerX,(canvas.getHeight()-bgPadding*2)*random,10,mCirclePaint);
        }
    }
}