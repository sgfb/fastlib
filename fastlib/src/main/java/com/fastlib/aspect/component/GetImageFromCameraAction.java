package com.fastlib.aspect.component;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.fastlib.aspect.AspectAction;
import com.fastlib.aspect.component.inject.GetImageFromCamera;
import com.fastlib.utils.ImageUtil;

/**
 * Created by sgfb on 2020\02\18.
 * 调用照相机拍照并返回照片路径
 */
public class GetImageFromCameraAction extends AspectAction<GetImageFromCamera>{

    @Override
    protected void handleAction(GetImageFromCamera anno, Object[] args){
        final Activity activity=getEnv(Activity.class);
        ActivityResultCallback.ActivityResultDelegate delegate=getEnv(ActivityResultCallback.ActivityResultDelegate.class);

        final CrossLock lock=obtainLock();
        delegate.addCallback(new ActivityResultCallback() {
            @Override
            public void onHandleActivityResult(int requestCode, int resultCode, Intent data) {
                if(resultCode==Activity.RESULT_OK){
                    Uri photoUri = ImageUtil.getImageFromActive(activity, requestCode, resultCode, data);
                    if (photoUri != null)
                        setResult(ImageUtil.getImagePath(activity, photoUri));
                    lock.unlock();
                }
            }
        });
        ImageUtil.openCamera(activity);
        lock.lock();
        setPassed(true);
    }
}
