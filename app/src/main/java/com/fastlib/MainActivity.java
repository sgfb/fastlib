package com.fastlib;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fastlib.app.FastApplication;
import com.fastlib.db.DataDelegater;
import com.fastlib.db.FastDatabase;
import com.fastlib.net.Listener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;
import com.fastlib.net.Result;
import com.fastlib.test.TestBean;
import com.fastlib.test.TestGlobal;
import com.fastlib.utils.N;
import com.fastlib.widget.SwipeRefreshWrapper;
import com.google.gson.Gson;
import com.google.gson.internal.Primitives;

import junit.framework.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bt=(Button)findViewById(R.id.bt);

        final DataDelegater delegater =new DataDelegater(TestBean.class,new Listener() {
            @Override
            public void onResponseListener(Result result) {
                System.out.println(result);
            }

            @Override
            public void onErrorListener(String error) {
                System.out.println("error:"+error);
            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegater.start();
            }
        });
    }
}