package com.fastlib;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.ImageView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.net.NetManager;
import com.fastlib.net.Request;
import com.fastlib.net.listener.SimpleListener;
import com.fastlib.utils.ContextHolder;
import com.fastlib.utils.Utils;
import com.fastlib.utils.monitor.MonitorService;

import java.io.File;
import java.util.Random;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    @Bind(R.id.image)
    ImageView mImage;
    final String mImageUrl="http://cn.best-wallpaper.net/wallpaper/3840x2160/1705/Earth-our-home-planet-space-black-background_3840x2160.jpg";
//    final String mImageUrl="https://static.oschina.net/uploads/img/201901/31055503_3yCJ.png";
    ImageRequest<String> ir=ImageRequest.create(mImageUrl);

    @Override
    public void alreadyPrepared(){
        ContextHolder.init(getApplicationContext());
        File file=new File(Environment.getExternalStorageDirectory(), Utils.getMd5(mImageUrl,false));
        file.delete();
    }

    @Bind(R.id.bt)
    private void bt(){
        ir.isCanceled=false;
        ir.mCallbackParcel=new Callback2ImageView(mImage);
        ir.start();
    }

    @Bind(R.id.bt2)
    private void bt2(){
        ir.cancel();
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