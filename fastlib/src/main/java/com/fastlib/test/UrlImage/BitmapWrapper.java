package com.fastlib.test.UrlImage;

import android.graphics.Bitmap;

/**
 * Created by sgfb on 18/1/6.
 * 包裹了除Bitmap之外的一些数据
 */
public class BitmapWrapper{
    public Bitmap bitmap;
    public int originWidth;  //图像源宽度
    public int originHeight;  //图像源高度
    public int sampleSize;  //与真实图像缩放比
    public byte[] compressData; //TODO temp
}