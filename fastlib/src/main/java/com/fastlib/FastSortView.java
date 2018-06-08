package com.fastlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.fastlib.net.NetManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by sgfb on 18/6/6.
 */
public class FastSortView extends View implements Sortable{
    private List<Integer> mValue=new LinkedList<>();
    private boolean isInit=false;
    private boolean isRunning=false;
    private Paint mPaint;

    public FastSortView(Context context) {
        super(context);
        init();
    }

    public FastSortView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        mPaint=new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getResources().getColor(R.color.green_600));
    }

    @Override
    public void reStart(){
        Random random=new Random();
        for(int i=0;i<MAX_VALUE;i++)
            mValue.add(random.nextInt(MAX_HEIGHT));
//        mValue.add(49);
//        mValue.add(38);
//        mValue.add(65);
//        mValue.add(97);
//        mValue.add(76);
//        mValue.add(13);
//        mValue.add(27);
//        mValue.add(49);
        start();
    }

    @Override
    public void resume() {

    }

    @Override
    public void stop() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(int i=0;i<mValue.size();i++)
            canvas.drawLine(i,0,i,mValue.get(i),mPaint);
    }

    private void start(){
        invalidate();
        NetManager.sRequestPool.execute(new Runnable() {
            @Override
            public void run() {
                resolve(0,mValue.size());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                resolve(0,MAX_VALUE);
            }
        });
    }

    private void resolve(int startIndex,int endIndex){
        if(Math.abs(startIndex-endIndex)>1){
            int key=mValue.get(startIndex);
            int index=startIndex;
            for(int i=startIndex+1;i<endIndex;i++){
                int value=mValue.get(i);
                if(value<key){
                    mValue.add(index,mValue.remove(i));
                    index++;
                }
            }
            post(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            });
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(index==0) index=1;
            else if(index==MAX_VALUE-1) index=MAX_VALUE-2;
            resolve(startIndex,index);
            resolve(index+1,endIndex);
        }
    }
}
