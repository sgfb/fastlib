package com.fastlib;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.image_manager.request.Callback2ImageView;
import com.fastlib.image_manager.request.ImageRequest;
import com.fastlib.utils.ContextHolder;
import com.fastlib.utils.monitor.MonitorService;

import java.io.File;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    @Bind(R.id.list)
    RecyclerView mList;
    @Bind(R.id.image)
    ImageView mImage;
    final String mImageUrl="http://cn.best-wallpaper.net/wallpaper/3840x2160/1705/Earth-our-home-planet-space-black-background_3840x2160.jpg";
//    final String mImageUrl="https://static.oschina.net/uploads/img/201901/31055503_3yCJ.png";
//
    @Override
    public void alreadyPrepared(){
        ContextHolder.init(getApplicationContext());
        requestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new Runnable() {
            @Override
            public void run() {

            }
        }, new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Bind(R.id.bt)
    private void bt(){
        mList.setAdapter(new MyAdapter());
    }

    @Bind(R.id.bt2)
    private void bt2(){
        ImageRequest.create(mImageUrl)
                .bindOnHostLifeCycle(this)
                .setCallbackParcel(new Callback2ImageView(mImage))
                .start();
    }

    @Bind(R.id.bt3)
    private void bt3(){
        startService(new Intent(this, MonitorService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}