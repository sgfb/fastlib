package com.fastlib;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.aspect.ControllerInvocationHandler;

import leo.android.cglib.proxy.Enhancer;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    MainController mMc;
    ControllerInvocationHandler mControllerInvocationhandler;

    @Bind(R.id.bt)
    private void bt(ViewGroup v) {
    }

    @Bind(R.id.bt2)
    private void bt2(){
        if(mMc!=null)
            mMc.doNow(this);
    }

    @Bind(R.id.bt3)
    private void bt3(){

    }

    @Override
    public void alreadyPrepared(){
        Enhancer enhancer=new Enhancer(this);
        enhancer.setSuperclass(MainController.class);
        enhancer.setInterceptor(mControllerInvocationhandler =new ControllerInvocationHandler());
        mMc= (MainController) enhancer.create();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}