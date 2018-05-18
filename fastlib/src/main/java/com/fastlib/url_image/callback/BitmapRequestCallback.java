package com.fastlib.url_image.callback;

import android.graphics.Bitmap;

import com.fastlib.url_image.request.ImageRequest;

/**
 * Created by sgfb on 18/1/22.
 * 图像请求回调
 */
public interface BitmapRequestCallback{

    /**
     * 图像请求成功回调方法
     * @param request 图像请求
     * @param bitmap 位图
     */
    void success(ImageRequest request, Bitmap bitmap);

    /**
     * 图像请求失败回调方法
     * @param request 图像请求
     */
    void failure(ImageRequest request);
}