package com.fastlib;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.Event;
import com.fastlib.app.FastActivity;
import com.fastlib.bean.EventDownloading;
import com.fastlib.bean.EventUploading;
import com.fastlib.db.FastDatabase;
import com.fastlib.net.DefaultDownload;
import com.fastlib.net.Listener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;
import com.fastlib.utils.N;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 16/5/10.
 */
public class MainActivity extends FastActivity{
    @Bind(R.id.et)
    EditText et;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FastDatabase.getDefaultInstance().getConfig().setOutInfomation(false);
    }

    @Bind(R.id.bt)
    public void save(View v){

    }

    @Bind(R.id.bt2)
    public void show(View v){

    }
}