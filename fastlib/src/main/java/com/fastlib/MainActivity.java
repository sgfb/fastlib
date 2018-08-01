package com.fastlib;

import android.content.Intent;
import android.os.Environment;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.fastlib.adapter.CommonViewPagerAdapter;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.app.task.NetAction;
import com.fastlib.app.task.Task;
import com.hhkj.mylibrary.LibActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

/**
 * Created by sgfb on 2018/7/25.
 */
@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{

    @Override
    public void alreadyPrepared() {

    }

    @Bind(R.id.bt)
    private void bt(){
        Intent intent=new Intent(this, LibActivity.class);
        startActivity(intent);
    }

    @Bind(R.id.bt2)
    private void bt2(){

    }
}
