package com.fastlib.aspect.component;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.fastlib.aspect.AspectAction;
import com.fastlib.aspect.ActionResult;
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

        delegate.setCallback(new ActivityResultCallback() {
            @Override
            public void onHandleActivityResult(int requestCode, int resultCode, Intent data) {
                if(resultCode!=Activity.RESULT_OK)
                    return;
                Uri photoUri = ImageUtil.getImageFromActive(activity, requestCode, resultCode, data);
                if (photoUri != null)
                    setResult(ImageUtil.getImagePath(activity, photoUri));
                synchronized (GetImageFromAlbumAction.this){
                    GetImageFromAlbumAction.this.notifyAll();
                }
            }
        });
        ImageUtil.openAlbum(activity);
        synchronized (this){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        setPassed(true);
    }
}
