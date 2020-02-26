package com.fastlib.aspect.component.opaque_action;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.fastlib.aspect.AspectAction;
import com.fastlib.aspect.component.ActivityResultReceiverGroup;
import com.fastlib.aspect.component.inject.GetImageFromAlbum;
import com.fastlib.aspect.event_callback.ThirdParamReceiver;
import com.fastlib.utils.ImageUtil;

import java.lang.reflect.Method;

/**
 * Created by sgfb on 2020\02\17.
 * 获取相册切面事件
 */
public class GetImageFromAlbumAction extends AspectAction<GetImageFromAlbum>{

    @Override
    protected void handleAction(GetImageFromAlbum anno, Method method,Object[] args){
        final Activity activity=getEnv(Activity.class);
        final ActivityResultReceiverGroup activityResultReceiverGroup=getEnv(ActivityResultReceiverGroup.class);

        final CrossLock lock=obtainLock();
        activityResultReceiverGroup.addEventCallback(new ThirdParamReceiver<Integer, Integer, Intent>() {
            @Override
            public void receiveEvent(Integer requestCode, Integer resultCode, Intent data) {
                if(resultCode==Activity.RESULT_OK&&requestCode==lock.getId()){
                    Uri photoUri = ImageUtil.getImageFromActive(activity, ImageUtil.REQUEST_FROM_ALBUM, resultCode, data);
                    if (photoUri != null)
                        setResult(ImageUtil.getImagePath(activity, photoUri));
                    lock.unlock();
                }
            }
        });
        ImageUtil.openAlbum(activity,lock.getId());
        lock.lock();
        setPassed(true);
    }
}
