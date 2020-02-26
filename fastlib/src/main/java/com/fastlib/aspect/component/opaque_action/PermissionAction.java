package com.fastlib.aspect.component.opaque_action;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.fastlib.annotation.Permission;
import com.fastlib.aspect.AspectAction;
import com.fastlib.aspect.component.PermissionResultReceiverGroup;
import com.fastlib.aspect.event_callback.ThirdParamReceiver;

import java.lang.reflect.Method;

/**
 * Created by sgfb on 2020\02\17.
 */
public class PermissionAction extends AspectAction<Permission> {

    @Override
    public void handleAction(Permission anno, Method method,Object[] args){
        AppCompatActivity activity=getEnv(AppCompatActivity.class);
        PermissionResultReceiverGroup permissionResultReceiverGroup=getEnv(PermissionResultReceiverGroup.class);

        final CrossLock lock=obtainLock();
        permissionResultReceiverGroup.addEventCallback(new ThirdParamReceiver<Integer, String[], int[]>() {
            @Override
            public void receiveEvent(Integer param1, String[] param2, int[] grantResults) {
                boolean success=true;
                for(int resultCode:grantResults){
                    if(resultCode!=PackageManager.PERMISSION_GRANTED){
                        success=false;
                        break;
                    }
                }
                setPassed(success);
                lock.unlock();
            }
        });
        ActivityCompat.requestPermissions(activity,anno.value(),lock.getId());
        lock.lock();
    }
}
