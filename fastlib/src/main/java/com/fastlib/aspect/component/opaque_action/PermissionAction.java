package com.fastlib.aspect.component.opaque_action;

import androidx.appcompat.app.AppCompatActivity;

import com.fastlib.annotation.Permission;
import com.fastlib.aspect.AspectAction;
import com.fastlib.aspect.component.PermissionResultReceiverGroup;
import com.fastlib.aspect.component.RuntimePermissionHandler;
import com.fastlib.aspect.event_callback.ThirdParamReceiver;

import java.lang.reflect.Method;

/**
 * Created by sgfb on 2020\02\17.
 * 运行时权限注解处理
 */
public class PermissionAction extends AspectAction<Permission> {

    @Override
    public void handleAction(Permission anno, Method method,Object[] args){
        AppCompatActivity activity=getEnv(AppCompatActivity.class);
        PermissionResultReceiverGroup permissionResultReceiverGroup=getEnv(PermissionResultReceiverGroup.class);
        final RuntimePermissionHandler runtimePermissionHandler=getEnv(RuntimePermissionHandler.class);

        final CrossLock lock=obtainLock();
        permissionResultReceiverGroup.addEventCallback(new ThirdParamReceiver<Integer, String[], int[]>() {
            @Override
            public void receiveEvent(Integer param1, String[] param2, int[] grantResults) {
                setPassed(runtimePermissionHandler.handleResponse(param1,param2,grantResults));
                if(param1==lock.getId())
                    lock.unlock();
            }
        });
        runtimePermissionHandler.requestPermission(activity,anno.value(),lock.getId());
        lock.lock();
    }
}
