package com.fastlib.url_image;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.fastlib.url_image.request.ImageRequest;

/**
 * Created by sgfb on 2018/5/17.
 * 回调包裹
 */
public class ImageParcel implements CallbackParcel{
    private ImageView mImage;

    public ImageParcel(ImageView image){
        mImage=image;
    }

    @Override
    public void prepareLoad(final ImageRequest request){
        mImage.post(new Runnable(){
            @Override
            public void run() {
                mImage.setImageDrawable(request.getReplaceDrawable());
            }
        });
    }

    @Override
    public void success(ImageRequest request, Bitmap bitmap){
        ImageRequest.ViewAnimator animator=request.getmAnimator();
        if(animator!=null&&!request.getResponseStatus().isFromMemory()) animator.animator(mImage);
        mImage.setImageBitmap(bitmap);
    }

    @Override
    public void failure(ImageRequest request) {
        mImage.setImageDrawable(request.getErrorDrawable());
    }
}
