package com.fastlib.url_image;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.fastlib.url_image.request.ImageRequest;

/**
 * Created by Administrator on 2018/5/17.
 */
public class ImageTarget extends Target<ImageView>{

    public ImageTarget(ImageView mSelf,String key) {
        super(mSelf,key);
    }

    @Override
    public void prepareLoad(final ImageRequest request){
        mSelf.post(new Runnable() {
            @Override
            public void run() {
                mSelf.setImageDrawable(request.getReplaceDrawable());
            }
        });
    }

    @Override
    public void success(ImageRequest request, Bitmap bitmap){
        ImageRequest.ViewAnimator animator=request.getmAnimator();
        if(animator!=null&&!request.getResponseStatus().isFromMemory()) animator.animator(mSelf);
        mSelf.setImageBitmap(bitmap);
    }

    @Override
    public void failure(ImageRequest request) {
        mSelf.setImageDrawable(request.getErrorDrawable());
    }
}
