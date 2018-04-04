package com.fastlib.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.renderscript.Float2;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.fastlib.utils.ScreenUtils;

/**
 * Created by sgfb on 2017/10/26.
 * 可缩放ViewGroup
 */
public class ScalableViewGroup extends FrameLayout{
    private final int SCALE_ANIMATOR_DURATION = 200; //缩放动画时间
    private final float MAX_SCALE = 3f; //最大放大尺寸
    private final float MIN_SCALE=0.5f; //最小缩小尺寸
    private final int EVENT_TYPE_NONE=0; //无事件类型
    private final int EVENT_TYPE_SCROLL=1; //滚动事件类型
    private final int EVENT_TYPE_SCALE=2; //缩放事件类型
    private final int EXEC_EVENT_DISTANCE =5; //移动一定像素距离后才开始执行事件（移动或缩放）

    int mEventType=EVENT_TYPE_NONE; //记录当前事件标志
    float mScale=1;
    float mScaleRatio;
    Float2[] mTouchPoint=new Float2[]{new Float2(),new Float2()}; //记录旧的触摸坐标
    GestureDetector mGestureDetector;

    public ScalableViewGroup(@NonNull Context context){
        super(context);
        init();
    }

    public ScalableViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
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
        mScaleRatio= (float) Math.hypot(ScreenUtils.getScreenWidth(),ScreenUtils.getScreenHeight())/2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        super.onTouchEvent(event);
        int action=event.getAction()&MotionEvent.ACTION_MASK;
        mGestureDetector.onTouchEvent(event);
        if(action==MotionEvent.ACTION_UP||action==MotionEvent.ACTION_CANCEL) //手指全抬起或是取消事件
            mEventType = EVENT_TYPE_NONE;
        else if(action==MotionEvent.ACTION_POINTER_UP) //多手指状态抬起
            mEventType = EVENT_TYPE_SCROLL;
        else if(action==MotionEvent.ACTION_DOWN){ //单指点中
            mEventType=EVENT_TYPE_SCROLL;
            mTouchPoint[0].x=event.getRawX();
            mTouchPoint[0].y=event.getRawY();
        }
        else if(action==MotionEvent.ACTION_POINTER_DOWN){ //多手指点中
            mEventType = EVENT_TYPE_SCALE;
            mTouchPoint[0].x=event.getX(0);
            mTouchPoint[0].y=event.getY(0);
            mTouchPoint[1].x=event.getX(1);
            mTouchPoint[1].y=event.getY(1);
        }
        else if(action==MotionEvent.ACTION_MOVE){ //手指开始滑动
            if(mEventType==EVENT_TYPE_SCROLL) scroll(event);
            else if(mEventType==EVENT_TYPE_SCALE) scale(event);
        }
        return true;
    }

    /**
     * 单指滚动事件
     * @param event 触摸事件
     */
    private void scroll(MotionEvent event){
        float diffX=event.getRawX()-mTouchPoint[0].x;
        float diffY=event.getRawY()-mTouchPoint[0].y;
        float translationX=getTranslationX()+diffX;
        float translationY=getTranslationY()+diffY;
        float distance= (float) Math.hypot(event.getRawX()-mTouchPoint[0].x,event.getRawY()-mTouchPoint[0].y);
        final float translationMaxX=(mScale-1)*getWidth()/2;
        final float translationMaxY=(mScale-1)*getHeight()/2;
        final float translationMinX=(mScale-1)*getWidth()/2*-1;
        final float translationMinY=(mScale-1)*getHeight()/2*-1;

        if(distance< EXEC_EVENT_DISTANCE) return;
        if(translationX<translationMinX) translationX=translationMinX;
        if(translationY<translationMinY) translationY=translationMinY;
        if(translationX>translationMaxX) translationX=translationMaxX;
        if(translationY>translationMaxY) translationY=translationMaxY;
        setTranslationX(translationX);
        setTranslationY(translationY);
        //处理完后记录点位置
        mTouchPoint[0].x=event.getRawX();
        mTouchPoint[0].y=event.getRawY();
    }

    /**
     * 双指缩放事件
     * @param event 触摸事件
     */
    private void scale(MotionEvent event){
        float oldDistance= (float) Math.hypot(mTouchPoint[0].x-mTouchPoint[1].x,mTouchPoint[0].y-mTouchPoint[1].y);
        float newDistance= (float) Math.hypot(event.getX(0)-event.getX(1),event.getY(0)-event.getY(1));
        float diffDistance=newDistance-oldDistance; //如果为正数，放大。负数缩小
        mScale=getScaleX()+diffDistance/mScaleRatio;

        if(Math.abs(diffDistance)<EXEC_EVENT_DISTANCE) return;
        if(mScale<MIN_SCALE) mScale=MIN_SCALE;
        if(mScale>MAX_SCALE) mScale=MAX_SCALE;
        setScaleX(mScale);
        setScaleY(mScale);
        //事件处理完毕后记录位置
        mTouchPoint[0].x=event.getX(0);
        mTouchPoint[0].y=event.getY(0);
        mTouchPoint[1].x=event.getX(1);
        mTouchPoint[1].y=event.getY(1);
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