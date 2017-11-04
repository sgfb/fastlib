package com.fastlib;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.renderscript.Float2;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by sgfb on 2017/10/26.
 */
public class ScalableViewGroup2 extends FrameLayout{
    private final int SCALE_ANIMATOR_DURATION = 200; //缩放动画时间
    private final float MAX_SCALE = 3f; //最大放大尺寸
    private final int EVENT_TYPE_NONE=0; //无事件类型
    private final int EVENT_TYPE_SCROLL=1; //滚动事件类型
    private final int EVENT_TYPE_SCALE=2; //缩放事件类型

    int mEventType=EVENT_TYPE_NONE;
    int mScale=1;
    Float2[] mTouchPoint=new Float2[]{new Float2(),new Float2()};
    GestureDetector mGestureDetector;

    public ScalableViewGroup2(@NonNull Context context){
        super(context);
        init();
    }

    public ScalableViewGroup2(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        mGestureDetector=new GestureDetector(getContext(),new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                doubleTap();
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        super.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            mEventType=EVENT_TYPE_SCROLL;
            mTouchPoint[0].x=event.getX();
            mTouchPoint[0].y=event.getY();
        }
        else if(event.getAction()==MotionEvent.ACTION_MOVE){

        }
        return true;
    }

    /**
     * 单指滚动事件
     * @param event
     */
    private void scroll(MotionEvent event){

    }

    /**
     * 双击事件，如果未缩放到最大等级，增加一个缩放等级，否则缩小到最小等级
     */
    private void doubleTap(){
        if(mScale<MAX_SCALE){
            mScale++;
            ObjectAnimator.ofFloat(this,"scaleX",mScale-1,mScale).setDuration(SCALE_ANIMATOR_DURATION).start();
            ObjectAnimator.ofFloat(this,"scaleY",mScale-1,mScale).setDuration(SCALE_ANIMATOR_DURATION).start();
        }
        else{
            ObjectAnimator.ofFloat(this,"scaleX",mScale,1).setDuration(SCALE_ANIMATOR_DURATION).start();
            ObjectAnimator.ofFloat(this,"scaleY",mScale,1).setDuration(SCALE_ANIMATOR_DURATION).start();
            mScale=1;
        }
    }
}
