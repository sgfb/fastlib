package com.fastlib;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fastlib.db.DataDelegater;
import com.fastlib.net.Listener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;
import com.fastlib.test.TestBean;
import com.fastlib.utils.N;
import com.fastlib.widget.SwipeRefreshWrapper;
import com.google.gson.Gson;

import java.util.Map;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button bt=(Button)findViewById(R.id.bt);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}