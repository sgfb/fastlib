package com.fastlib.url_image;

import androidx.annotation.NonNull;

import com.fastlib.url_image.bean.FastImageConfig;
import com.fastlib.url_image.pool.TargetReference;
import com.fastlib.url_image.request.ImageRequest;

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

    private static void checkInstance(){
        if(mInstance==null) mInstance=new FastImage();
    }

    /**
     * 发起一个图像请求
     * @param request 图像请求
     */
    public static synchronized void request(ImageRequest request){
        checkInstance();
        if(mInstance.mTargetReference ==null){
            mInstance.mTargetReference =new TargetReference();
            mInstance.mProcessingManager=new ImageProcessManager(mInstance.mTargetReference);
        }
        mInstance.mProcessingManager.addBitmapRequest(request);
    }

    /**
     * 清理内存中的引用
     */
    public static void clearMemory(){
        checkInstance();
        if(mInstance.mTargetReference !=null)
        mInstance.mTargetReference.clear();
    }

    public static TargetReference getTargetReference(){
        checkInstance();
        return mInstance.mTargetReference;
    }

    public static  @NonNull FastImageConfig getConfig(){
        checkInstance();
        try {
            return mInstance.mConfig.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return new FastImageConfig();
    }

    public static void setConfig(@NonNull FastImageConfig config){
        checkInstance();
        mInstance.mConfig=config;
    }
}
