package com.fastlib;

import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.net.Request;
import com.fastlib.net.SimpleListener;
import com.fastlib.test.UrlImage.request.BitmapRequest;
import com.fastlib.test.UrlImage.FastImage;
import com.fastlib.test.UrlImage.FastImageConfig;
import com.fastlib.test.UrlImage.request.BitmapRequestEntrance;
import com.fastlib.utils.NetUtils;
import com.fastlib.widget.AutofitGridView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    @Bind(R.id.refresh)
    SwipeRefreshLayout mRefresh;
    @Bind(R.id.list)
    RecyclerView mList;
//    @Bind(R.id.image)
//    ImageView mImage;
//    @Bind(R.id.image2)
//    ImageView mImage2;
//    @Bind(R.id.image3)
//    ImageView mImage3;

    @Override
    protected void alreadyPrepared(){
        FastImageConfig config=FastImage.getInstance().getConfig();
        config.mSaveFolder=getExternalCacheDir();
        FastImage.getInstance().setConfig(config);
        final MyAdapter adapter=new MyAdapter(this);
        adapter.setRefreshLayout(mRefresh);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.refresh();
            }
        });
        mList.setAdapter(adapter);
    }

//    @Bind(R.id.bt)
    private void commit(){
//        BitmapRequest br=BitmapRequestEntrance.factory(this)
//                .bitmapRequestByUrl("https://static.oschina.net/uploads/space/2018/0106/134811_VkaD_347223.jpg")
//                .setImageView(mImage)
//                .setStoreStrategy(FastImageConfig.STRATEGY_STORE_SAVE_DISK)
//                .setSpecifiedStoreFile(new File(Environment.getExternalStorageDirectory(),"temp.jpg"));
//        FastImage.getInstance().startRequest(br);
    }

//    @Bind(R.id.bt2)
    private void commit2(){
//        BitmapRequest br=BitmapRequestEntrance.factory(this)
//                .bitmapRequestByUrl("https://static.oschina.net/uploads/space/2018/0106/134811_VkaD_347223.jpg")
//                .setImageView(mImage2)
//                .setRequestWidth(100)
//                .setRequestHeight(100)
//                .setSpecifiedStoreFile(new File(Environment.getExternalStorageDirectory(),"temp.jpg"));
//        FastImage.getInstance().startRequest(br);
    }

//    @Bind(R.id.bt3)
    private void commit3(){
//        BitmapRequest br=BitmapRequestEntrance.factory(this)
//                .bitmapRequestByUrl("http://c.hiphotos.baidu.com/image/pic/item/bd315c6034a85edfef0cf9e940540923dc547573.jpg")
//                .setImageView(mImage3);
//        FastImage.getInstance().startRequest(br);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FastImage.getInstance().clearMemory();
    }
}