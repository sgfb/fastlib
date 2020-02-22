package com.fastlib.aspect.component;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.fastlib.aspect.AspectAction;
import com.fastlib.aspect.component.inject.SycStartActivityForResult;
import com.fastlib.aspect.event_callback.ThirdParamReceiver;

/**
 * Created by sgfb on 2020\02\19.
 * 同步调起新Activity并且等待返回
 */
public class SycStartActivityForResultAction extends AspectAction<SycStartActivityForResult>{

    @Override
    protected void handleAction(final SycStartActivityForResult anno, Object[] args) {
//        ActivityResultCallback.ActivityResultDelegate delegate=getEnv(ActivityResultCallback.ActivityResultDelegate.class);
        Activity activity=getEnv(Activity.class);
        ActivityResultReceiverGroup activityEventReceivers=getEnv(ActivityResultReceiverGroup.class);

        final CrossLock lock=obtainLock();
        activityEventReceivers.addEventCallback(new ThirdParamReceiver<Integer, Integer, Intent>() {
            @Override
            public void receiveEvent(Integer param1, Integer param2, Intent param3) {
                if(param1==lock.getId()&&param2==Activity.RESULT_OK){
                    Bundle bundle=param3.getExtras();
                    setResult(bundle!=null?bundle.get(anno.resultKey()):null);
                    lock.unlock();
                }
            }
        });
        activity.startActivityForResult(new Intent(activity,anno.value()),lock.getId());
        lock.lock();
        setPassed(true);
    }
}
