package com.fastlib;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fastlib.db.FastDatabase;
import com.fastlib.net.Listener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;
import com.fastlib.test.TestBean;
import com.fastlib.utils.N;
import com.fastlib.widget.SwipeRefreshWrapper;
import com.google.gson.Gson;
import com.google.gson.internal.Primitives;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button bt=(Button)findViewById(R.id.bt);
        tv=(TextView)findViewById(R.id.textView);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestBean tb=new TestBean();
                tb.setData("world");
                FastDatabase.getInstance().saveOrUpdate(tb);
            }
        });
    }
}