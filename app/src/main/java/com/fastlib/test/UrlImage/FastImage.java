package com.fastlib.test.UrlImage;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.fastlib.test.UrlImage.request.BitmapRequest;

/**
 * Created by sgfb on 2017/11/5.
 * 图像加载工具包起点
 */
public class FastImage{
    private static FastImageConfig mConfig; //全局配置
    private static FastImage mInstance;
    private BitmapReferenceManager mBitmapReferenceManager;
    private ImageProcessManager mProcessingManager;

    private FastImage(){
        mConfig=new FastImageConfig();
    }

    public static synchronized FastImage getInstance(){
        if(mInstance==null) mInstance=new FastImage();
        return mInstance;
    }

    /**
     * 发起一个图像请求
     * @param request 图像请求
     */
    public void startRequest(BitmapRequest request){
        Object host=request.getHost();

        if(host==null) throw new IllegalArgumentException("BitmapRequest's host must not be null!");

        if(mBitmapReferenceManager==null){
            Context context=null;
            if(host instanceof Activity)
                context= (Context) host;
            else if(host instanceof Fragment)
                context=((Fragment)host).getContext();
            mBitmapReferenceManager=new BitmapReferenceManager(context);
            mProcessingManager=new ImageProcessManager(mBitmapReferenceManager);
        }
        mProcessingManager.addBitmapRequest(request);
    }

    /**
     * 清理内存中的引用
     */
    public void clearMemory(){
        if(mBitmapReferenceManager!=null)
        mBitmapReferenceManager.clear();
    }

    public static @NonNull FastImageConfig getConfig(){
        try {
            return mConfig.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return new FastImageConfig();
    }

    public void setConfig(@NonNull FastImageConfig config){
        mConfig=config;
    }
}
