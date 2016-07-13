package com.fastlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.renderscript.Float2;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 16/7/12.
 */
public class AudioMap extends View{
    private Float2[] mMapping;
    private Paint mPaint;
    private Path path=new Path();

    public AudioMap(Context context){
        super(context);
    }

    public AudioMap(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint=new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(3.0f);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void onDraw(Canvas canvas){
        filterInflection();
        if(mMapping!=null&&mMapping.length>=2){
            for(int i=0;i<mMapping.length-1;i++){
                path.reset();
                Float2 start=mMapping[i];
                Float2 end=mMapping[i+1];
                float middleX=(start.x+end.x)/2;
                float middleY=(start.y+end.y)/5;

                path.moveTo(start.x,start.y);
                path.quadTo(middleX,middleY,end.x,end.y);
                canvas.drawPath(path,mPaint);
            }
        }
    }

    /**
     * 过滤出拐点
     */
    private void filterInflection(){
        if(mMapping==null||mMapping.length<=1)
            return;
        List<Float2> list=new ArrayList<>();
        list.add(mMapping[0]);
        int index=nextInflection(0);
        while(index<mMapping.length-1) {
            list.add(mMapping[index = nextInflection(index + 1)]);
//            if(index<mMapping.length-1)
//                list.add(mMapping[index+1]);
        }
        mMapping=list.toArray(new Float2[list.size()]);
    }

    private int nextInflection(int index){
        if(index+1>=mMapping.length-1)
            return mMapping.length-1;
        boolean increment=mMapping[index].y<mMapping[index+1].y;
        for(int i=index+1;i<mMapping.length-1;i++){
            if((increment&&mMapping[i].y>mMapping[i+1].y)||(!increment&&mMapping[i].y<mMapping[i+1].y))
                return i-1;
        }
        return mMapping.length-1;
    }

    public void setMapping(Float2[] mapping){
        mMapping=mapping;
        invalidate();
    }
}