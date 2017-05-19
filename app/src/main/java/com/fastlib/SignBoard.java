package com.fastlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.fastlib.utils.DensityUtils;
import com.fastlib.utils.ScreenUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 17/5/15.
 * 签名板
 */
public class SignBoard extends SurfaceView implements SurfaceHolder.Callback,View.OnTouchListener{
    private List<List<Point>> mPoints;
    private SurfaceHolder mHolder;
    private Paint mPenPaint;
    private Bitmap mBitmap;

    public SignBoard(Context context) {
        super(context);
        init();
    }

    public SignBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        getHolder().addCallback(this);
        setOnTouchListener(this);
        mPenPaint=new Paint();
        mPoints=new ArrayList<>();
        mBitmap=Bitmap.createBitmap(ScreenUtils.getScreenWidth(),ScreenUtils.getScreenHeight(),Bitmap.Config.ARGB_4444);

        mPenPaint.setColor(Color.BLACK);
        mPenPaint.setStyle(Paint.Style.STROKE);
        mPenPaint.setStrokeWidth(10);
    }

    private void drawTrace(){
        Canvas lockCanvas=mHolder.lockCanvas();
        if(lockCanvas==null) return;
        Canvas canvas=new Canvas(mBitmap);
        canvas.drawColor(Color.WHITE);

        for(List<Point> oneLine:mPoints){
            Path path=new Path();

            if(oneLine.size()>1){
                boolean first=true;
                for(Point p:oneLine) {
                    if(first){
                        first=false;
                        path.moveTo(p.x,p.y);
                    }
                    else
                        path.lineTo(p.x, p.y);
                }
            }
            canvas.drawPath(path,mPenPaint);
        }
        lockCanvas.drawBitmap(mBitmap,0,0,mPenPaint);
        mHolder.unlockCanvasAndPost(lockCanvas);
    }

    public void clear(){
        mPoints.clear();
        drawTrace();
    }

    public void save(File file){
        if(file==null||file.exists()){
            System.out.println("文件不存在");
            return;
        }
        try {
            OutputStream out=new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG,100,out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                List<Point> list=new ArrayList<>();
                list.add(new Point((int)event.getX(),(int)event.getY()));
                mPoints.add(list);
                break;
            case MotionEvent.ACTION_MOVE:
                mPoints.get(mPoints.size()-1).add(new Point((int)event.getX(),(int)event.getY()));
                drawTrace();
                break;
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHolder=holder;
        Canvas canvas=holder.lockCanvas();
        canvas.drawColor(Color.WHITE);
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mBitmap.recycle();
        mBitmap=null;
    }
}
