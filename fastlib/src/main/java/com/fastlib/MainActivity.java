package com.fastlib;

import android.support.v4.view.ViewPager;
import android.webkit.WebView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.db.FastDatabase;
import com.fastlib.image_manager.ImageManager;
import com.fastlib.image_manager.bean.ImageConfig;
import com.fastlib.net.DefaultDownload;
import com.fastlib.net.NetManager;
import com.fastlib.net.Request;
import com.fastlib.net.listener.SimpleListener;
import com.fastlib.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity {

    @Bind(R.id.bt)
    private void startServer(){
        new Request("get","http://www.baidu.com").start();
    }

    @Bind(R.id.bt2)
    private void bt2(){

    }

    @Override
    public void alreadyPrepared() {
        NetManager.getInstance().setGlobalListener(new AppGlobalListener());
    }
}