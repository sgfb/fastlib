package com.fastlib;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2017/10/25.
 */
@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    @Bind(R.id.grid)
    RecyclerView mGrid;

    @Override
    protected void alreadyPrepared() {
        Random random=new Random();
        mGrid.setLayoutManager(new GridLayoutManager(this,8));
        List<Bean> bean=new ArrayList<>();
        bean.add(new Bean(true," "));
        bean.add(new Bean(true,"日"));
        bean.add(new Bean(true,"一"));
        bean.add(new Bean(true,"二"));
        bean.add(new Bean(true,"三"));
        bean.add(new Bean(true,"四"));
        bean.add(new Bean(true,"五"));
        bean.add(new Bean(true,"六"));
        bean.add(new Bean(true,"上午"));
        for(int i=0;i<7;i++){
            bean.add(new Bean(random.nextBoolean(),""));
        }
        bean.add(new Bean(true,"下午"));
        for(int i=0;i<7;i++){
            bean.add(new Bean(random.nextBoolean(),""));
        }
        bean.add(new Bean(true,"下午"));
        for(int i=0;i<7;i++){
            bean.add(new Bean(random.nextBoolean(),""));
        }
        mGrid.setAdapter(new MyAdapter(this,bean));
    }

    @Bind(R.id.bt)
    private void commit(){

     }
}
