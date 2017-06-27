package com.fastlib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.Image;
import android.media.ImageReader;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.ImageView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.EventObserver;
import com.fastlib.app.FastActivity;

import java.io.IOException;

/**
 * Created by sgfb on 17/6/27.
 */
@ContentView(R.layout.act_camera)
public class CameraActivity extends FastActivity{
    @Bind(R.id.surfaceView)
    GLSurfaceView mSurfaceView;
    @Bind(R.id.image)
    ImageView mImage;
    boolean isPreviewing;
    Camera mCamera;

    @Bind(R.id.bt)
    private void preview(){
        if(isPreviewing)
            return;
        mCamera.startPreview();
        isPreviewing =true;
    }
    @Override
    protected void alreadyPrepared(){
        mCamera=Camera.open();
        try {
            mCamera.setPreviewDisplay(mSurfaceView.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera){
                BitmapFactory.Options options=new BitmapFactory.Options();
                //预览数据
                mImage.setImageBitmap(BitmapFactory.decodeByteArray(data,0,data.length,options));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isPreviewing)
            mCamera.stopPreview();
        mCamera.setPreviewCallback(null);
        mCamera.unlock();
        mCamera.release();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        EventObserver.build(this);
        super.onCreate(savedInstanceState);
    }
}