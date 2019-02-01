package com.fastlib;

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
     * @param data
     */
    void success(ImageRequest request,byte[] data);

    /**
     * 请求失败
     * @param request
     */
    void failure(ImageRequest request);
}