package com.fastlib;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.JsonToken;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fastlib.adapter.BindingAdapter;
import com.fastlib.adapter.BindingAdapter2;
import com.fastlib.net.Downloadable;
import com.fastlib.net.Listener;
import com.fastlib.net.NetProcessor;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;
import com.fastlib.utils.BindingView;
import com.fastlib.widget.RecyclerListView;
import com.fastlib.widget.RoundImageView;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 16/5/10.
 *
 */
public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

            }
        });
    }
}