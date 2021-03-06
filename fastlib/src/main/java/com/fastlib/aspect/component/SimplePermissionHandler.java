package com.fastlib.aspect.component;

import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;

import com.fastlib.aspect.base.StaticProvider;

/**
 * Created by sgfb on 2020\02\27.
 * 默认的运行时权限获取请求
 */
@StaticProvider
public class SimplePermissionHandler implements RuntimePermissionHandler {

    @Override
    public void requestPermission(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity,permissions,requestCode);
    }

    @Override
    public boolean handleResponse(Integer intent, String[] permissions, int[] grantResults) {
        for(int resultCode:grantResults){
            if(resultCode!=PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}
