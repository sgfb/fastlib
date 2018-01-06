package com.fastlib.test.UrlImage;

import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.text.TextUtils;

import com.fastlib.utils.Utils;

import java.io.File;

/**
 * Created by sgfb on 2017/11/4.
 * Bitmap请求类
 * 如果指定宽高.按照指定宽高的centerCrop读取
 * 如果没有指定宽高(width和height都是0),则尝试读取ImageView宽高,如果ImageView宽高也读取不到，载入一个小于屏幕尺寸的图像.
 * 如果指定宽高为(-1,-1),读取原图宽高到内存中
 */
public class BitmapRequest{
    private boolean isStoreRealName; //存储为图片真实名
    private int mRequestWidth;
    private int mRequestHeight;
    private int mStoreStrategy=FastImageConfig.STORE_STRATEGY_DEFAULT;
    private String mUrl;
    private File mSpecifiedStoreFile; //指定下载位置
    private Bitmap.Config mBitmapConfig=Bitmap.Config.RGB_565;
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

    public Status getStatus(){
        return mStatus;
    }

    public static File getSaveFile(BitmapRequest request){
        return request.getSpecifiedStoreFile()!=null?request.getSpecifiedStoreFile():
                new File(FastImage.getInstance().getConfig().mSaveFolder,Utils.getMd5(request.getUrl(),false));
    }

    public String getKey(){
        return Utils.getMd5(getUrl(),false);
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