package com.fastlib.test.UrlImage;

import android.graphics.Bitmap;
import android.graphics.Point;

import com.fastlib.utils.ScreenUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sgfb on 2017/11/4.
 * Bitmap池管理，FastUrlImage的Bitmap都从池中进池中出，默认两屏幕缓存大小
 */
public class BitmapPool {
    private long mPoolSize; //池容量,字节单位
    private long mPoolRemain;
    private Map<String,Bitmap> mPool; //key键bitmap值

    BitmapPool(){
        Point screenSize=ScreenUtils.getScreenSize();
        mPool=new HashMap<>();
        mPoolSize=mPoolRemain=screenSize.x*screenSize.y*2;
    }

    /**
     * 设置池大小
     * @param poolSize 池最大容量
     */
    public void setPoolSize(long poolSize){
        if(poolSize<0) poolSize=0;
        while(mPoolRemain>=poolSize)
            pop();
        mPoolSize=poolSize;
    }

    /**
     * 根据key值取Bitmap
     * @param key url md5（32位）后的值
     * @return 对应Bitmap
     */
    public Bitmap getBitmap(String key){
        return mPool.get(key);
    }

    /**
     * 增加图像到池
     * @param key 键
     * @param bitmap 图像
     */
    public void addBitmap(String key,Bitmap bitmap){
        if(mPool.containsKey(key))
            mPool.get(key).recycle();
        mPool.put(key,bitmap);
    }

    public boolean containBitmap(String key){
        return mPool.containsKey(key);
    }

    /**
     * 清空池
     */
    public void clear(){
        for(Map.Entry<String,Bitmap> entry:mPool.entrySet())
            entry.getValue().recycle();
        mPool.clear();
    }

    /**
     * 移出一个引用数最少的Bitmap
     */
    private void pop(){

    }
}