package com.fastlib.url_image;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.fastlib.url_image.request.BitmapRequest;

/**
 * Created by Administrator on 2018/5/17.
 */
public class ImageTarget extends Target<ImageView>{

    public ImageTarget(ImageView mSelf,String key) {
        super(mSelf,key);
    }

    @Override
    public void prepareLoad(final BitmapRequest request){
        mSelf.post(new Runnable() {
            @Override
            public void run() {
                mSelf.setImageDrawable(request.getReplaceDrawable());
            }
        });
    }

    @Override
    public void success(BitmapRequest request, Bitmap bitmap) {
        mSelf.setImageBitmap(bitmap);
    }

    @Override
    public void failure(BitmapRequest request) {
        mSelf.setImageDrawable(request.getErrorDrawable());
    }
}
