package com.fastlib;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.ImageView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.aspect.component.ActivityResultReceiverGroup;
import com.fastlib.aspect.AspectManager;
import com.fastlib.aspect.ControllerInvocationHandler;
import com.fastlib.aspect.component.ActivityResultCallback;
import com.fastlib.aspect.component.PermissionCallback;
import com.fastlib.aspect.component.SimpleAspectCacheManager;

import leo.android.cglib.proxy.Enhancer;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity {
    @Bind(R.id.image)
    ImageView mImage;
    @Bind(R.id.editText)
    EditText mEditText;
    MainController mMc;
    PermissionCallback.PermissionDelegate mPermissionDelegate=new PermissionCallback.PermissionDelegate();
    ActivityResultCallback.ActivityResultDelegate mActivityDelegate=new ActivityResultCallback.ActivityResultDelegate();
    ActivityResultReceiverGroup mActivityCallbackHolder=new ActivityResultReceiverGroup();

    @Bind(R.id.bt)
    private void bt() {
        new Thread(){
            @Override
            public void run() {
                System.out.println("返回:"+mMc.startSecondActivityWaitResult());
            }
        }.start();
    }

    @Bind(R.id.bt2)
    private void bt2(){

    }

    @Bind(R.id.bt3)
    private void bt3(){

    }

    @Override
    public void alreadyPrepared(){
        AspectManager am=AspectManager.getInstance();
        am.addAspectActions(this,"com.fastlib.aspect");

        Enhancer enhancer=new Enhancer(this);
        enhancer.setSuperclass(MainController.class);
        enhancer.setInterceptor(new ControllerInvocationHandler());
        mMc= (MainController) enhancer.create();
        mMc.addEnvs(this);
        mMc.addEnvs(mPermissionDelegate);
        mMc.addEnvs(mActivityDelegate);
        mMc.addEnvs(mActivityCallbackHolder);
        mMc.addEnvs(new SimpleAspectCacheManager());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mActivityCallbackHolder.sendEvent(requestCode,resultCode,data);
        if(!mActivityDelegate.getCallback().isEmpty()){
            for(ActivityResultCallback callback:mActivityDelegate.getCallback())
                callback.onHandleActivityResult(requestCode,resultCode,data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(mPermissionDelegate.getCallback()!=null)
            mPermissionDelegate.getCallback().onPermissionResult(requestCode,permissions,grantResults);
    }
}