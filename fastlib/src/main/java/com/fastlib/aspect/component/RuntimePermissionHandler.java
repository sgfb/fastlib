package com.fastlib.aspect.component;

import android.app.Activity;

/**
 * Created by sgfb on 2020\02\27.
 * 运行时权限获取发起请求
 */
public interface RuntimePermissionHandler {

    /**
     * 发起请求
     */
    void requestPermission(Activity activity,String[] permissions,int requestCode);

    /**
     * 处理返回
     * @return true 权限获取成功,false不成功
     */
    boolean handleResponse(Integer intent, String[] permissions, int[] grantResults);
}
