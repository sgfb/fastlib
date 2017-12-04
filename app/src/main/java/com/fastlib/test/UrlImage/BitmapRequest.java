package com.fastlib.test.UrlImage;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by sgfb on 2017/11/4.
 * Bitmap请求类
 */
public class BitmapRequest{
    private boolean isStoreRealName; //存储为图片真实名
    private int mRequestWidth;
    private int mRequestHeight;
    private String mUrl;
    private String mKey; //url MD5 32位后数据
    private File mSpecifiedStoreFile; //指定下载位置
    private StoreStrategy mStrategy=StoreStrategy.DEFAULT;
    private Bitmap.Config mBitmapConfig=Bitmap.Config.ARGB_4444;
    private Status mStatus=Status.PREPARE;
    //TODO 动画占位

    public BitmapRequest(){}

    public BitmapRequest(String url){
        mUrl=url;
    }

    public boolean isStoreRealName() {
        return isStoreRealName;
    }

    public void setStoreRealName(boolean storeRealName) {
        isStoreRealName = storeRealName;
    }

    public int getRequestWidth() {
        return mRequestWidth;
    }

    public void setRequestWidth(int requestWidth) {
        //width>0
        mRequestWidth = requestWidth;
    }

    public int getRequestHeight() {
        return mRequestHeight;
    }

    public void setRequestHeight(int requestHeight) {
        //height>0
        mRequestHeight = requestHeight;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public File getSpecifiedStoreFile() {
        return mSpecifiedStoreFile;
    }

    public void setSpecifiedStoreFile(File specifiedStoreFile) {
        mSpecifiedStoreFile = specifiedStoreFile;
    }

    public Bitmap.Config getBitmapConfig() {
        return mBitmapConfig;
    }

    public void setBitmapConfig(Bitmap.Config bitmapConfig) {
        mBitmapConfig = bitmapConfig;
    }

    public StoreStrategy getStrategy() {
        return mStrategy;
    }

    public void setStrategy(StoreStrategy strategy) {
        mStrategy = strategy;
    }

    public Status getStatus(){
        return mStatus;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof BitmapRequest){
            BitmapRequest other= (BitmapRequest) o;
            return TextUtils.equals(other.getUrl(),mUrl)&&
                    other.getRequestWidth()==mRequestWidth&&
                    other.getRequestHeight()==mRequestHeight;
        }
        else return false;
    }

    public enum  Status{
        PREPARE,
        RUNNING,
        COMPLETE,
        FAILURE
    }
}