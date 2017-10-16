package com.fastlib.local_test;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fastlib.R;
import com.fastlib.adapter.CommonFragmentViewPagerAdapter;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.net.Request;
import com.fastlib.net.SimpleListener;
import com.fastlib.utils.N;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 2017/9/21.
 */
@ContentView(R.layout.act_main2)
public class MainActivity extends FastActivity{
    @Bind(R.id.viewPager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(3000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            firstLoad();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    protected void alreadyPrepared(){
        final List<Pair<String,Fragment>> list=new ArrayList<>();
        list.add(Pair.<String, Fragment>create("a",new MyFragment()));
        list.add(Pair.<String, Fragment>create("b",new MyFragment()));
        list.add(Pair.<String, Fragment>create("c",new MyFragment()));
        list.add(Pair.<String, Fragment>create("d",new MyFragment()));
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(new CommonFragmentViewPagerAdapter(getSupportFragmentManager(),list));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((MyFragment)list.get(position).second).firstLoad();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Bind(R.id.bt)
    private void commit(View view){
        ViewGroup contentView= (ViewGroup) findViewById(android.R.id.content);
        for(int i=0;i<contentView.getChildCount();i++)
            System.out.println(contentView.getChildAt(i));
    }

    @Bind(R.id.bt2)
    private void commit2(View view){

    }

//    @Override
//    protected View generateDeferLoadingView(){
//        return LayoutInflater.from(this).inflate(R.layout.loading,null);
//    }
}