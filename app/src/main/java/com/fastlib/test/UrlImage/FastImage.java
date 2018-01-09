package com.fastlib.test.UrlImage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

/**
 * Created by sgfb on 2017/11/5.
 */
public class FastImage{
    private static FastImageConfig mConfig; //全局配置
    private static FastImage mInstance;
    private BitmapReferenceManager mBitmapReferenceManager;
    private ImageProcessingManager mProcessingManager;

    private FastImage(Context context){
        mConfig=new FastImageConfig();
        mBitmapReferenceManager=new BitmapReferenceManager(context);
        mProcessingManager=new ImageProcessingManager(mBitmapReferenceManager);
    }

    public static synchronized FastImage getInstance(Context context){
        if(mInstance==null) mInstance=new FastImage(context.getApplicationContext());
        return mInstance;
    }

    /**
     * 清理内存中的引用
     */
    public void clearMemory(){
        mBitmapReferenceManager.clear();
    }

    public void startRequest(Context context,final BitmapRequest request, final ImageView imageView){
        request.setRequestWidth(imageView.getWidth());
        request.setRequestHeight(imageView.getHeight());
        mProcessingManager.addBitmapRequest(context,request,imageView);
    }

    public BitmapReferenceManager getBitmapReferenceManager(){
        return mBitmapReferenceManager;
    }

    public static FastImageConfig getConfig(){
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
