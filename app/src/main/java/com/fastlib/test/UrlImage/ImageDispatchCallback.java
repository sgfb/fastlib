package com.fastlib.test.UrlImage;

/**
 * Created by sgfb on 2017/11/5.
 * 图像从url或路径中转换成Bitmap后回调
 */
public interface ImageDispatchCallback{

    /**
     * 图像准备完毕回调
     * @param request 图像请求
     * @param wrapper bitmap和扩展属性
     */
    void complete(BitmapRequest request,BitmapWrapper wrapper);
}