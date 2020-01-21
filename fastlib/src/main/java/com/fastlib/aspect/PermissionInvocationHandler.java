package com.fastlib.aspect;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.fastlib.annotation.Permission;
import com.fastlib.utils.PermissionHelper;

import java.lang.reflect.Method;

/**
 * Created by sgfb on 2020\01\10.
 * 运行时权限流程控制
 */
public class PermissionInvocationHandler extends InvocationHandlerNode<Permission>{
    private static final Object sLock=new Object();
    private PermissionHelper mPermissionHelper=new PermissionHelper();
    private Activity mActivity;
    private Fragment mFragment;
    private boolean mPermissionRefused=false;

    public PermissionInvocationHandler(Permission mAnnotation, InvocationHandlerNode mNext) {
        super(mAnnotation, mNext);
    }

    @Override
    protected void handle(Object host, Class proxyClass, Method proxyMethod) throws Exception {
        mPermissionHelper.requestPermission(mActivity, mFragment, mAnnotation.value(), new Runnable() {
            @Override
            public void run() {
                synchronized (sLock) {
                    mPermissionRefused=false;
                    sLock.notify();
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                synchronized (sLock){
                    mPermissionRefused=true;
                    sLock.notify();
                }
            }
        });
        synchronized (sLock){
            sLock.wait();
        }
        if(mPermissionRefused)
            throw new PermissionAspectException(mAnnotation.value());
        startNext(null);
    }

    public void onPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        mPermissionHelper.permissionResult(requestCode,permissions,grantResults);
    }
}
