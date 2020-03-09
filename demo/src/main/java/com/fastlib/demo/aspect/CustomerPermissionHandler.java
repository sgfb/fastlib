package com.fastlib.demo.aspect;

import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;

import com.fastlib.aspect.base.StaticProvider;
import com.fastlib.aspect.component.RuntimePermissionHandler;

/**
 * Created by sgfb on 2020\02\27.
 */
@StaticProvider
public class CustomerPermissionHandler implements RuntimePermissionHandler{

    @Override
    public void requestPermission(Activity activity, String[] permissions, int requestCode) {
        System.out.println("自定义权限请求");
        ActivityCompat.requestPermissions(activity,permissions,requestCode);
    }

    @Override
    public boolean handleResponse(Integer intent, String[] permissions, int[] grantResults) {
        for (int grantResult : grantResults)
            if (grantResult == PackageManager.PERMISSION_DENIED) return false;
        return true;
    }
}
