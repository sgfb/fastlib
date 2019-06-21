package com.fastlib;

import android.Manifest;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.view.View;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity {

    @Bind(R.id.bt)
    private void startServer(){
        startActivity(new Intent(this,ServiceActivity.class));
    }

    @Bind(R.id.bt2)
    private void bt2(){
        startActivity(new Intent(this,ClientActivity.class));
    }

    @Bind(R.id.bt3)
    private void bt3(){

    }

    @Override
    public void alreadyPrepared() {

    }
}