package com.fastlib.alpha;

import android.app.Activity;

import com.fastlib.aspect.StaticProvier;
import com.fastlib.aspect.component.RuntimePermissionHandler;

/**
 * Created by sgfb on 2020\02\27.
 */
@StaticProvier
public class CustomerPermissionHandler implements RuntimePermissionHandler{

    @Override
    public void requestPermission(Activity activity, String[] permissions, int requestCode) {
        System.out.println("自定义权限请求");
    }

    @Override
    public boolean handleResponse(Integer intent, String[] permissions, int[] grantResults) {
        return false;
    }
}
