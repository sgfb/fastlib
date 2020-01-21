package com.fastlib.aspect;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.fastlib.utils.ImageUtil;

import java.lang.reflect.Method;

/**
 * Created by sgfb on 2020\01\13.
 */
public class ImageFromAlbumAspectHandler extends InvocationHandlerNode<GetImageFromAlbum>{
    private Activity mActivity;
    private String mResultPhoto;

    public ImageFromAlbumAspectHandler(GetImageFromAlbum mAnnotation, InvocationHandlerNode mNext) {
        super(mAnnotation, mNext);
    }

    @Override
    protected void handle(Object host, Class proxyClass, Method proxyMethod) throws Exception {
        ImageUtil.openAlbum(mActivity);
    }

    public void onImageResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK)
            return;
        Uri photoUri = ImageUtil.getImageFromActive(mActivity, requestCode, resultCode, data);
        if (photoUri != null)
            mResultPhoto= ImageUtil.getImagePath(mActivity, photoUri);
        startNext(mResultPhoto);
    }
}
