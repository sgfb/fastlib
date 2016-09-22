package com.fastlib;


import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fastlib.app.FastActivity;
import com.fastlib.base.JsonActivity;
import com.fastlib.net.Request;
import com.fastlib.utils.JsonBinder;
import com.fastlib.widget.FastSwipeRefresh;

import java.util.List;

/**
 * Created by sgfb on 16/5/10.
 */
public class MainActivity extends FastActivity{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}