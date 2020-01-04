package com.fastlib;

import android.content.Intent;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.PhotoResultListener;
import com.fastlib.app.module.FastActivity;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{

    @Bind(R.id.bt)
    private void bt() {
        openCamera(new PhotoResultListener() {
            @Override
            public void onPhotoResult(String path) {

            }
        });
    }

    @Bind(R.id.bt2)
    private void bt2(){
        openAlbum(new PhotoResultListener() {
            @Override
            public void onPhotoResult(String path) {

            }
        });
    }

    @Bind(R.id.bt3)
    private void bt3(){

    }

    @Override
    public void alreadyPrepared() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(Thread.currentThread().getName());
    }
}