package com.fastlib;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

/**
 * Created by sgfb on 19/2/12.
 * E-mail: 602687446@qq.com
 * ImageView回调包裹
 */
public class Callback2ImageView implements CallbackParcel{
    private ImageView mImageView;

    public Callback2ImageView(ImageView imageView){
        mImageView=imageView;
    }

    @Override
    public void prepareLoad(ImageRequest request){
        mImageView.setImageDrawable(request.mReplaceDrawable);
    }

    @Override
    public void success(ImageRequest request, final Bitmap bitmap){
        Handler handler=new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                mImageView.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public void failure(ImageRequest request, Exception exception) {
        mImageView.setImageDrawable(request.mErrorDrawable);
    }
}
