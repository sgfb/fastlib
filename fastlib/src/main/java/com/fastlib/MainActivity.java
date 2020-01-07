package com.fastlib;

import android.content.Intent;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.PhotoResultListener;
import com.fastlib.app.module.FastActivity;
import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.net2.Request;
import com.fastlib.net2.SimpleListener;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{

    @Bind(R.id.bt)
    private void bt() {

    }

    @Bind(R.id.bt2)
    private void bt2(){

    }

    @Bind(R.id.bt3)
    private void bt3(){

    }

    @Override
    public void alreadyPrepared() {

    }

}