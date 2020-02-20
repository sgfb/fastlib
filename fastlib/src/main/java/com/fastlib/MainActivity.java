package com.fastlib;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.aspect.AspectManager;
import com.fastlib.aspect.ControllerInvocationHandler;
import com.fastlib.aspect.component.ActivityResultCallback;
import com.fastlib.aspect.component.Logcat;
import com.fastlib.aspect.component.PermissionCallback;
import com.fastlib.aspect.component.SimpleAspectCacheManager;
import com.fastlib.net2.MultiSegmentDownloadController;
import com.fastlib.net2.Request;
import com.fastlib.net2.SimpleListener;
import com.fastlib.net2.SingleDownloadController;

import java.io.File;

import leo.android.cglib.proxy.Enhancer;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity {
    public static final String RES_STR_NAME="name";

    @Bind(R.id.image)
    ImageView mImage;
    @Bind(R.id.editText)
    EditText mEditText;
    MainController mMc;
    PermissionCallback.PermissionDelegate mPermissionDelegate=new PermissionCallback.PermissionDelegate();
    ActivityResultCallback.ActivityResultDelegate mActivityDelegate=new ActivityResultCallback.ActivityResultDelegate();

    @Bind(R.id.bt)
    private void bt() {
        ThreadPoolManager.sQuickPool.execute(new Runnable() {
            @Override
            public void run() {
                MultiSegmentDownloadController.startMultiDownload(new Request("http://192.168.3.20:8082/image.jpg"),new File(getExternalCacheDir(),"image.jpg"));
            }
        });
    }

    @Bind(R.id.bt2)
    private void bt2(){
        ThreadPoolManager.sSlowPool.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("image path:"+mMc.getImageFromAlbum());
            }
        });
    }

    @Bind(R.id.bt3)
    private void bt3(){
        ThreadPoolManager.sSlowPool.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(mMc.startMainActivityWaitResult());
            }
        });
    }

    @Override
    public void alreadyPrepared(){
        AspectManager am=AspectManager.getInstance();
        am.putTransparentAction(Logcat.class, new Runnable() {
            @Override
            public void run() {
                Log.d("aspect","just say hello");
            }
        });
        am.addAspectActions(this,"com.fastlib.aspect");

        Enhancer enhancer=new Enhancer(this);
        enhancer.setSuperclass(MainController.class);
        enhancer.setInterceptor(new ControllerInvocationHandler());
        mMc= (MainController) enhancer.create();
        mMc.addEnvs(this);
        mMc.addEnvs(mPermissionDelegate);
        mMc.addEnvs(mActivityDelegate);
        mMc.addEnvs(new SimpleAspectCacheManager());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(mActivityDelegate.getCallback()!=null)
            mActivityDelegate.getCallback().onHandleActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(mPermissionDelegate.getCallback()!=null)
            mPermissionDelegate.getCallback().onPermissionResult(requestCode,permissions,grantResults);
    }
}