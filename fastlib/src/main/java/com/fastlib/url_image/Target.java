package com.fastlib.url_image;

import android.graphics.Bitmap;

import com.fastlib.url_image.request.ImageRequest;

/**
 * Created by Administrator on 2018/5/17.
 * 使用请求返还图像对象
 */
public abstract class Target<T>{
    protected T mSelf;
    protected String mKey;

    public Target(T mSelf,String key){
        this.mSelf = mSelf;
        mKey=key;
    }

    /**
     * 请求前
     * @param request
     */
    public abstract void prepareLoad(ImageRequest request);

    /**
     * 请求成功
     * @param request
     * @param bitmap
     */
    public abstract void success(ImageRequest request, Bitmap bitmap);

    /**
     * 请求失败
     * @param request
     */
    public abstract void failure(ImageRequest request);

    public String getKey(){
        return mKey;
    }

    @Override
    public boolean equals(Object o) {
        return this==o||(o instanceof Target&&mSelf.equals(((Target) o).mSelf));
    }
}