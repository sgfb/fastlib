package com.fastlib;

import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.app.task.ThreadPoolManager;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    @Bind(R.id.surfaceView)
    SurfaceView mSurfaceView;
    VideoDecoder mVideoDecoder;
    VideoEncoder mVideoEncoder;
    List<byte[]> mFrameList=new ArrayList<>();
    MediaProjectionManager mMediaProjectionManager;
    MediaProjection mMediaProject;

    @Override
    public void alreadyPrepared(){
        mMediaProjectionManager= (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(),1);
        mVideoEncoder=new VideoEncoder();
        mVideoDecoder=new VideoDecoder();
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    @Bind(R.id.bt)
    private void startServer(){
        mSurfaceView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFrameList.clear();
                mVideoEncoder.start();
                ThreadPoolManager.sSlowPool.execute(new Runnable() {
                    @Override
                    public void run(){
                        while(!mVideoEncoder.isClosed()){
                            byte[] data=mVideoEncoder.pollFrameFromEncoder();
                            if(data!=null){
                                System.out.println("add frame:"+data.length);
                                mFrameList.add(data);
                            }
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                mSurfaceView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("record end");
                        mVideoEncoder.close();
                    }
                },6000);
            }
        },2000);
    }

    @Bind(R.id.bt2)
    private void startClient(){
        System.out.println("frame size:"+mFrameList.size());
        mVideoDecoder.start(mSurfaceView.getHolder().getSurface());
        ThreadPoolManager.sSlowPool.execute(new Runnable() {
            @Override
            public void run() {
                for(byte[] frame:mFrameList)
                    mVideoDecoder.addData(frame);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK){
            mMediaProject=mMediaProjectionManager.getMediaProjection(resultCode,data);
            mMediaProject.createVirtualDisplay("screenRecode",480,800,260,0,mVideoEncoder.getSurface(),null,null);
        }
    }
}