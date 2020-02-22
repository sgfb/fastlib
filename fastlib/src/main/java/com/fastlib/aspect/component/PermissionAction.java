package com.fastlib.aspect.component;

import android.content.pm.PackageManager;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.fastlib.annotation.Permission;
import com.fastlib.aspect.AspectAction;
import com.fastlib.aspect.exception.EnvMissingException;

/**
 * Created by sgfb on 2020\02\17.
 */
public class PermissionAction extends AspectAction<Permission> {
    private boolean isPassed=false;

    @Override
    public void handleAction(Permission anno, Object[] args){
        AppCompatActivity activity=getEnv(AppCompatActivity.class);
        PermissionCallback.PermissionDelegate delegate=getEnv(PermissionCallback.PermissionDelegate.class);

        if(activity==null||delegate==null) throw new EnvMissingException(anno.getClass());
        if(Thread.currentThread()==Looper.getMainLooper().getThread()) throw new IllegalStateException("不支持在主线程中使用权限切面");

        delegate.setCallback(new PermissionCallback() {
            @Override
            public void onPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                boolean success=true;
                for(int resultCode:grantResults){
                    if(resultCode!=PackageManager.PERMISSION_GRANTED){
                        success=false;
                        break;
                    }
                }
                isPassed=success;
                synchronized (PermissionAction.this){
                    PermissionAction.this.notifyAll();
                }
            }
        });
        ActivityCompat.requestPermissions(activity,anno.value(),101);
        synchronized (this){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        setPassed(isPassed);
    }
}
