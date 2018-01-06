package com.fastlib.test.UrlImage;

import android.support.annotation.NonNull;

/**
 * Created by sgfb on 2017/11/5.
 */
public class FastImage{
    private FastImageConfig mConfig; //全局配置
    private static FastImage mInstance;
    private BitmapReferenceManager mBitmapReferenceManager;

    private FastImage(){
        mConfig=new FastImageConfig();
    }

    public static synchronized FastImage getInstance(){
        if(mInstance==null) mInstance=new FastImage();
        return mInstance;
    }

    public void startRequest(BitmapRequest request){

    }

    public BitmapReferenceManager getBitmapReferenceManager(){
        return mBitmapReferenceManager;
    }

    public FastImageConfig getConfig(){
        try {
            return mConfig.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setConfig(@NonNull FastImageConfig config){
        mConfig=config;
    }
}
