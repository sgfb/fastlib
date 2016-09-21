package com.fastlib;


import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.Event;
import com.fastlib.app.FastActivity;
import com.fastlib.bean.EventDownloading;
import com.fastlib.net.DefaultDownload;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;

import java.io.File;
import java.io.IOException;

/**
 * Created by sgfb on 16/5/10.
 */
public class MainActivity extends FastActivity{
    @Bind(id=R.id.text)
    TextView tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Bind(id=R.id.bt)
    public void onclick(View v){

    }

    @Bind(id=R.id.bt2)
    public void onclick2(View v){

    }

    @Event
    public void downloading(EventDownloading loading){

    }
}