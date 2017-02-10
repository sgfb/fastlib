package com.fastlib;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;

import com.fastlib.adapter.JsonAdapter;
import com.fastlib.annotation.Bind;
import com.fastlib.app.FastActivity;
import com.fastlib.app.FastDialog;
import com.fastlib.net.DefaultMockProcessor;
import com.fastlib.net.Listener;
import com.fastlib.net.Request;
import com.fastlib.utils.N;
import com.fastlib.utils.json.FastJson;
import com.fastlib.utils.json.JsonObject;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 16/12/29.
 */
public class MainActivity extends FastActivity{
    @Bind(R.id.image)
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Bind(R.id.bt)
    public void commit(View v){
        Drawable ic=DrawableCompat.wrap(getResources().getDrawable(R.mipmap.ic_launcher));
        DrawableCompat.setTint(ic,getResources().getColor(R.color.Blue_500));
        iv.setImageDrawable(ic);
    }

    @Bind(R.id.bt2)
    public void commit2(View v){

    }
}