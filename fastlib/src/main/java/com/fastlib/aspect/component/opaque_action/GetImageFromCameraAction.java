package com.fastlib.aspect.component.opaque_action;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.fastlib.aspect.AspectAction;
import com.fastlib.aspect.component.ActivityResultReceiverGroup;
import com.fastlib.aspect.component.inject.GetImageFromCamera;
import com.fastlib.aspect.event_callback.ThirdParamReceiver;
import com.fastlib.utils.ImageUtil;

import java.lang.reflect.Method;

/**
 * Created by sgfb on 2020\02\18.
 * 调用照相机拍照并返回照片路径
 */
public class GetImageFromCameraAction extends AspectAction<GetImageFromCamera>{

    @Override
    protected void handleAction(GetImageFromCamera anno, Method method,Object[] args){
        final Activity activity=getEnv(Activity.class);
        ActivityResultReceiverGroup activityResultReceiverGroup=getEnv(ActivityResultReceiverGroup.class);

        final CrossLock lock=obtainLock();
        activityResultReceiverGroup.addEventCallback(new ThirdParamReceiver<Integer, Integer, Intent>() {
            @Override
            public void receiveEvent(Integer requestCode, Integer resultCode, Intent data) {
                if(resultCode==Activity.RESULT_OK&&requestCode==lock.getId()){
                    Uri photoUri = ImageUtil.getImageFromActive(activity, requestCode, resultCode, data);
                    if (photoUri != null)
                        setResult(ImageUtil.getImagePath(activity, photoUri));
                    lock.unlock();
                }
            }
        });
        ImageUtil.openCamera(activity,lock.getId());
        lock.lock();
        setPassed(true);
    }
}
