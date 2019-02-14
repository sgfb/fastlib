package com.fastlib.url_image.request;

import android.graphics.Bitmap;

/**
 * Created by sgfb on 2018/5/17.
 * 使用请求返还图像对象
 */
public interface CallbackParcel {

    /**
     * 请求前
     * @param request
     */
    void prepareLoad(ImageRequest request);

    /**
     * 请求成功
     * @param request
     * @param bitmap
     */
    void success(ImageRequest request, Bitmap bitmap);

    /**
     * 请求失败
     * @param request
     */
    void failure(ImageRequest request,Exception exception);
}