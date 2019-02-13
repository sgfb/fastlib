package com.fastlib;

import android.util.LongSparseArray;

/**
 * 图形资源引用
 */
public class ImageRef{
    private LongSparseArray<String> mViewId2ImageName=new LongSparseArray<>();

    public LongSparseArray<String> getViewId2ImageName(){
        return mViewId2ImageName;
    }
}
