package com.fastlib;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.fastlib.annotation.ContentView;
import com.fastlib.utils.ScreenUtils;

/**
 * Created by Administrator on 2018/5/18.
 */
@ContentView(R.layout.act_main)
public class LauncherActivity extends AppCompatActivity{
    MediaProjectionManager mMediaProjectionManager;
    boolean stopFlag=false;
    Image mImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LauncherBinding binding=DataBindingUtil.setContentView(this,R.layout.act_main);
        Bean bean=new Bean();
        bean.name="sgfb";
        binding.setBean(bean);
        binding.setCtrl(this);
        ImageView iv;
    }

    public void captureView(View v){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            MediaProjectionManager mMediaProjectionManager= (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(),1);
        }
    }

    public void stop(View v){
        stopFlag=true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK&&Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            MediaProjection mp=mMediaProjectionManager.getMediaProjection(resultCode,data);
            VirtualDisplay vd=mp.createVirtualDisplay("display",480,800, 420,DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,null,null,null);

            if(stopFlag) mp.stop();
        }
    }
}