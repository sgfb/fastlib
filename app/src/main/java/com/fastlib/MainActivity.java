package com.fastlib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.fastlib.adapter.CommonFragmentViewPagerAdapter;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;

    @Override
    protected void alreadyPrepared(){
        List<Pair<String,Fragment>> list=new ArrayList<>();
        list.add(Pair.<String, Fragment>create("a",new MyFragment()));
        list.add(Pair.<String, Fragment>create("b",new MyFragment()));
        list.add(Pair.<String, Fragment>create("c",new MyFragment()));
        mViewPager.setAdapter(new CommonFragmentViewPagerAdapter(getSupportFragmentManager(),list));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Bind(R.id.bt)
    private void commit(){

    }

    @Bind(R.id.bt2)
    private void commit2(){

    }
}