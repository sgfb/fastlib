package com.fastlib.url_image;

import android.support.annotation.NonNull;

import com.fastlib.url_image.bean.FastImageConfig;
import com.fastlib.url_image.pool.TargetReference;
import com.fastlib.url_image.request.BitmapRequest;

/**
 * Created by sgfb on 2017/11/5.
 * 图像加载工具包起点
 */
public class FastImage{
    private static FastImage mInstance;
    private FastImageConfig mConfig; //全局配置
    private TargetReference mTargetReference;
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
        if(mTargetReference ==null){
            mTargetReference =new TargetReference(request.getContext());
            mProcessingManager=new ImageProcessManager(mTargetReference);
        }
        mProcessingManager.addBitmapRequest(request);
    }

    /**
     * 清理内存中的引用
     */
    public void clearMemory(){
        if(mTargetReference !=null)
        mTargetReference.clear();
    }

    public TargetReference getTargetReference(){
        return mTargetReference;
    }

    public @NonNull FastImageConfig getConfig(){
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
