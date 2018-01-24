package com.fastlib.test.UrlImage;

import com.fastlib.test.UrlImage.request.BitmapRequest;

/**
 * Created by sgfb on 2017/11/5.
 * 图像从url或路径中转换成Bitmap后回调
 */
public interface ImageDispatchCallback{

    /**
     * 图像准备完毕回调
     * @param processing 图像处理具体实例(某个步骤)
     * @param request 图像请求
     * @param wrapper bitmap和扩展属性
     */
    void complete(UrlImageProcessing processing, BitmapRequest request, BitmapWrapper wrapper);
}