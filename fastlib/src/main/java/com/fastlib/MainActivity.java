package com.fastlib;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import com.fastlib.adapter.CommonFragmentViewPagerAdapter;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.db.FastDatabase;
import com.fastlib.db.SaveUtil;
import com.fastlib.image_manager.ImageManager;
import com.fastlib.image_manager.bean.ImageConfig;
import com.fastlib.net.DefaultDownload;
import com.fastlib.net.NetManager;
import com.fastlib.net.Request;
import com.fastlib.net.listener.GlobalListener;
import com.fastlib.net.listener.SimpleListener;
import com.fastlib.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity {

    @Bind(R.id.bt)
    private void startServer(){
        Request request=new Request("www.baidu.com","get");
        request.setListener(new SimpleListener<String>(){

            @Override
            public void onResponseListener(Request r, String result) {
                System.out.println("response:"+r.hashCode());
            }
        });
        System.out.println("raw hs:"+request.hashCode());
        request.put("a","b");
        System.out.println("start hs:"+request.hashCode());
        request.start();
    }

    @Bind(R.id.bt2)
    private void bt2(){

    }

    @Override
    public void alreadyPrepared() {
        NetManager.getInstance().setRootAddress("http://");
    }
}