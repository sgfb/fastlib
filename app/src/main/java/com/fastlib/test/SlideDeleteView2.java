package com.fastlib.test;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

/**
 * Created by sgfb on 16/2/26.
 */
public class SlideDeleteView2 extends FrameLayout{
    private float mLastX,mDownX;
    private View mMainView,mDeleteView;
    private boolean isMoving=false;

    public SlideDeleteView2(Context context){
        this(context,null);
    }

    public SlideDeleteView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        //没有子视图时跳出
        if(getChildCount()<=0)
            return super.dispatchTouchEvent(ev);
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownX=mLastX=ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                View v=getChildAt(1);
                float offset=v.getTranslationX()+ev.getX()- mLastX;
                mLastX=ev.getX();
                if(v.getX()+offset>0)
                    v.setTranslationX(0);
                else
                    v.setTranslationX(offset);
                break;
            case MotionEvent.ACTION_UP:
                isMoving=Math.abs(mDownX-ev.getX())>10;
                startAnimate(Math.abs(mMainView.getTranslationX())<mDeleteView.getWidth());
                ListView parent= (ListView) getParent();
                for(int i=0;i<parent.getChildCount();i++){
                    View child=parent.getChildAt(i);
                    if(child==null||!(child instanceof SlideDeleteView2))
                        continue;
                    ((SlideDeleteView2)child).close();
                }
                break;
        }
        super.dispatchTouchEvent(ev);
        return true;
    }

    @Override
    public void addView(View child){
        super.addView(child);
        if(getChildCount()>=2)
            throw new IllegalStateException("Can't add more than 2 views to a SlidingView");
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDeleteView=getChildAt(0);
        mMainView=getChildAt(1);
        setContentListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("main view has been clicked");
            }
        });
    }

    private void startAnimate(boolean toLeft){
        int end=toLeft?0:-mDeleteView.getWidth();
        ObjectAnimator oa=ObjectAnimator.ofFloat(mMainView,"translationX",mMainView.getTranslationX(),end).setDuration(200);
        oa.start();
    }

    public void close(){
        mMainView.setTranslationX(0);
    }

    public void setContentListener(final OnClickListener listener){
        mMainView.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!isMoving)
                    listener.onClick(v);
            }
        });
    }
}
