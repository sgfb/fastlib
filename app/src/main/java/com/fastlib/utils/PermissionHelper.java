package com.fastlib.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.fastlib.annotation.Permission;
import com.fastlib.bean.PermissionRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 17/2/21.
 * 6.0权限获取辅助类
 */
public class PermissionHelper{
    private Map<String,PermissionRequest> mPermissionMap = new HashMap<>();
    private Map<String,LazyPermissionMethod> mLazyCallMethods=new HashMap<>(); //调起的方法名-->请求权限及其后续动作包裹
    private Activity mActivity;
    private Object mLazyHost;

    public PermissionHelper(Activity activity){
        mActivity = activity;
    }

    /**
     * 6.0后请求权限
     * @param permission 权限名
     * @param grantedAfterProcess 成功后回调
     * @param deniedAfterProcess 失败后回调
     */
    public void requestPermission(String permission, Runnable grantedAfterProcess, Runnable deniedAfterProcess) {
        if (ContextCompat.checkSelfPermission(mActivity,permission) == PackageManager.PERMISSION_GRANTED)
            grantedAfterProcess.run();
        else {
            if (!mPermissionMap.containsKey(permission)) {
                int requestCode = mPermissionMap.size() + 1;
                mPermissionMap.put(permission, new PermissionRequest(requestCode, grantedAfterProcess, deniedAfterProcess));
                ActivityCompat.requestPermissions(mActivity, new String[]{permission}, requestCode);
            }
        }
    }

    public void permissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        for (int i = 0; i < permissions.length; i++){
            PermissionRequest pr = mPermissionMap.remove(permissions[i]);
            if (pr != null) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    pr.hadPermissionProcess.run();
                else
                    pr.deniedPermissionProcess.run();
                break;
            }
        }
    }

    /**
     * 循环请求所有权限，如果有一个权限请求失败即调起失败回调或者显示失败信息
     * @param lpm
     * @param permissions
     */
    private void recursiveRequestPermission(final LazyPermissionMethod lpm, final List<String> permissions){
        if(permissions.isEmpty()){
            if(lpm.successMethod!=null)
                try {
                    lpm.successMethod.invoke(mLazyHost);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
        }
        else{
            requestPermission(permissions.get(0), new Runnable() {
                @Override
                public void run() {
                    permissions.remove(0);
                    recursiveRequestPermission(lpm,permissions);
                }
            }, new Runnable() {
                @Override
                public void run() {
                    if(lpm.failureMethod==null) //如果权限请求失败回调不存在，显示失败提示信息
                        N.showShort(mActivity,lpm.failureMessage);
                    else try {
                        lpm.failureMethod.invoke(mLazyHost);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 调起延迟请求权限
     * @param name
     */
    public void callLazyPermission(String name){
        if(mLazyCallMethods.containsKey(name)){
            final LazyPermissionMethod lazyPermission=mLazyCallMethods.get(name);
            List<String> list= new ArrayList<>();

            for(String permission:lazyPermission.permissions)
                list.add(permission);
            recursiveRequestPermission(lazyPermission,list);
        }
    }

    /**
     * 自动填充Permission注解
     * @param obj 被Permission注解对象
     */
    public void checkPermissionInject(Object obj){
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M) //如果小于6.0，不需要运行时权限
            return;

        mLazyHost=obj;
        Method[] methods=obj.getClass().getDeclaredMethods();

        for(Method m:methods){
            Permission inject=m.getAnnotation(Permission.class);
            String methodName=m.getName();
            if(inject!=null){
                String[] requestPermissions=inject.value();
                LazyPermissionMethod lm=mLazyCallMethods.get(methodName);
                if(lm==null){
                    lm=new LazyPermissionMethod();
                    mLazyCallMethods.put(methodName,lm);
                }
                lm.permissions=requestPermissions;
                lm.failureMessage=inject.print();
                if(inject.type()==0) lm.successMethod=m;
                else lm.failureMethod=m;
            }
        }
    }

    public class LazyPermissionMethod{
        public String[] permissions;
        public Method successMethod,failureMethod;
        public String failureMessage;

        public LazyPermissionMethod() {}
    }
}
