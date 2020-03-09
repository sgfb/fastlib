package com.fastlib.demo;

import android.content.Context;

import com.fastlib.adapter.CommonFragmentViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.lxj.xpopup.core.BottomPopupView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by sgfb on 2020\03\08.
 */
public class MyBottomPop extends BottomPopupView{

    public MyBottomPop(Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.custom_tab_layout_text;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        initView();
    }

    public void initView(){
        TabLayout tabLayout=findViewById(R.id.tabLayout);
        ViewPager viewPager=findViewById(R.id.viewPager);
        tabLayout.setupWithViewPager(viewPager);

        List<Pair<String,Fragment>> pages=new ArrayList<>();
        pages.add(Pair.<String, Fragment>create("page1",new ListFragment()));
        pages.add(Pair.<String, Fragment>create("page2",new ListFragment()));
        viewPager.setAdapter(new CommonFragmentViewPagerAdapter(((AppCompatActivity)getContext()).getSupportFragmentManager(),pages));
    }
}
