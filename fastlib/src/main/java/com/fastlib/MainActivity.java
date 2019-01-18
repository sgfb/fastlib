package com.fastlib;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Process;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.annotation.Event;
import com.fastlib.app.module.FastActivity;
import com.fastlib.bean.event.EventDownloading;
import com.fastlib.net.DefaultDownload;
import com.fastlib.net.MonitorThreadPool;
import com.fastlib.net.NetManager;
import com.fastlib.net.Request;
import com.fastlib.net.listener.GlobalListener;
import com.fastlib.net.listener.SimpleListener;
import com.fastlib.utils.monitor.MonitorService;
import com.fastlib.utils.monitor.Requesting;
import com.fastlib.utils.monitor.RequestingAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Create by sgfb on 2018/11/5.
 * E-mail:602687446@qq.com
 */
@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity {

    @Override
    public void alreadyPrepared() {

    }

    @Bind(R.id.bt)
    private void bt() {

    }

    @Bind(R.id.bt2)
    private void bt2() {

    }

    @Bind(R.id.bt3)
    private void bt3(){

    }
}