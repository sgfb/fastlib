package com.fastlib.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.fastlib.bean.PermissionRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sgfb on 17/2/21.
 * 6.0权限获取辅助类
 */
public class PermissionHelper{
    private Map<String,PermissionRequest> mPermissionMap = new HashMap<>();
    private Activity mActivity;

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
        if (ContextCompat.checkSelfPermission(mActivity, permission) == PackageManager.PERMISSION_GRANTED)
            grantedAfterProcess.run();
        else {
            if (!mPermissionMap.containsKey(permission)) {
                int requestCode = mPermissionMap.size() + 1;
                mPermissionMap.put(permission, new PermissionRequest(requestCode, grantedAfterProcess, deniedAfterProcess));
                ActivityCompat.requestPermissions(mActivity, new String[]{permission}, requestCode);
            }
        }
    }

    public void permissioResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
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
}
