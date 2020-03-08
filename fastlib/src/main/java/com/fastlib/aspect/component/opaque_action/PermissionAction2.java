package com.fastlib.aspect.component.opaque_action;

import android.support.v7.app.AppCompatActivity;

import com.fastlib.annotation.Permission;
import com.fastlib.aspect.component.AspectRuntimeArg;
import com.fastlib.aspect.component.PermissionResultReceiverGroup;
import com.fastlib.aspect.component.RuntimePermissionHandler;
import com.fastlib.aspect.component.SimpleAspectAction;
import com.fastlib.aspect.event_callback.ThirdParamReceiver;

import java.lang.reflect.Method;

/**
 * Created by sgfb on 2020\03\08.
 * TODO
 */
public class PermissionAction2 extends SimpleAspectAction<Permission>{
    @AspectRuntimeArg(AppCompatActivity.class)
    AppCompatActivity mActivity;
    @AspectRuntimeArg(RuntimePermissionHandler.class)
    RuntimePermissionHandler mPermissionHandler;
    @AspectRuntimeArg(PermissionResultReceiverGroup.class)
    PermissionResultReceiverGroup mPermissionResultReceiverGroup;

    @Override
    protected void handle(Permission anno, Method method, Object[] args) {
        mPermissionResultReceiverGroup.addEventCallback(new ThirdParamReceiver<Integer, String[], int[]>() {
            @Override
            public void receiveEvent(Integer param1, String[] param2, int[] param3) {
                if(param1==getLockId()){
                    unlock();
                }
            }
        });
        mPermissionHandler.requestPermission(mActivity,anno.value(),getLockId());
    }
}
