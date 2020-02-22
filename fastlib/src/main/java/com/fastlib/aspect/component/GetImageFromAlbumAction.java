package com.fastlib.aspect.component;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.fastlib.aspect.AspectAction;
import com.fastlib.aspect.component.inject.GetImageFromAlbum;
import com.fastlib.aspect.exception.EnvMissingException;
import com.fastlib.utils.ImageUtil;

/**
 * Created by sgfb on 2020\02\17.
 * 获取相册切面事件
 */
public class GetImageFromAlbumAction extends AspectAction<GetImageFromAlbum>{

    @Override
    protected void handleAction(GetImageFromAlbum anno, Object[] args){
        final Activity activity=getEnv(Activity.class);
        ActivityResultCallback.ActivityResultDelegate delegate=getEnv(ActivityResultCallback.ActivityResultDelegate.class);

        if(activity==null||delegate==null) throw new EnvMissingException(anno.getClass());

        final CrossLock lock=obtainLock();
        delegate.addCallback(new ActivityResultCallback() {
            @Override
            public void onHandleActivityResult(int requestCode, int resultCode, Intent data) {
                if(resultCode==Activity.RESULT_OK&&requestCode==lock.getId()){
                    Uri photoUri = ImageUtil.getImageFromActive(activity, requestCode, resultCode, data);
                    if (photoUri != null)
                        setResult(ImageUtil.getImagePath(activity, photoUri));
                    lock.unlock();
                }
            }
        });
        ImageUtil.openAlbum(activity);
        lock.lock();
        setPassed(true);
    }
}
