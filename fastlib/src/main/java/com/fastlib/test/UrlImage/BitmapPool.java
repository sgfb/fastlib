package com.fastlib.test.UrlImage;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.Pair;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 2017/11/4.
 * Bitmap池管理.默认可用内存的5分之一
 */
public class BitmapPool {
    private long mPoolSize; //池容量,字节单位
    private long mPoolRemain;
    private Map<String,BitmapWrapper> mPool; //key键bitmap值
    private BitmapReferenceManager mBitmapReferenceManager;

    BitmapPool(Context context,BitmapReferenceManager bitmapReferenceManager){
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        mPool=new HashMap<>();
        mBitmapReferenceManager=bitmapReferenceManager;

        mPoolSize=mPoolRemain=am.getMemoryClass()*1024*205;  //可用内存的5分之1
        System.out.println("生成Bitmap池 池总容量:"+(mPoolSize/1024)+"KB");
    }

    /**
     * 设置池大小
     * @param poolSize 池最大容量
     */
    public void setPoolSize(long poolSize){
        if(poolSize<=0) throw new IllegalArgumentException("pool size must bigger than 0");
        while(mPoolRemain>=poolSize)
            pop();
        mPoolSize=poolSize;
    }

    /**
     * 根据key值取Bitmap包裹
     * @param key url md5（32位）后的值
     * @return 对应Bitmap包裹
     */
    public BitmapWrapper getBitmapWrapper(String key){
        return mPool.get(key);
    }

    /**
     * 增加图像到池
     * @param key 键
     * @param wrapper 图像包裹
     */
    public void addBitmap(String key,BitmapWrapper wrapper){
        Bitmap bitmap=wrapper.bitmap;
        if(mPool.containsKey(key))
            removeBitmap(key);
        mPool.put(key,wrapper);
        mPoolRemain-=bitmap.getByteCount();
        System.out.println("Bitmap入池 申请"+(bitmap.getByteCount()/1024)+"KB 剩余池容量:"+(mPoolRemain/1024)+"KB");
    }

    public boolean containBitmap(String key){
        return mPool.containsKey(key);
    }

    /**
     * 清空池
     */
    void clear(){
        for(Map.Entry<String,BitmapWrapper> entry:mPool.entrySet())
            entry.getValue().bitmap.recycle();
        mPool.clear();
        mPoolRemain=mPoolSize;
    }

    /**
     * 移出一个空或最少引用的Bitmap
     */
    private void pop(){
        Map<String,List<ImageView>> reference=mBitmapReferenceManager.getReference();
        if(!reference.isEmpty()){
            String leastReferKey=findLeastReferKey(reference);
            removeBitmap(leastReferKey);
        }
    }

    /**
     * 移除一个Bitmap出池
     * @param key 对应键
     */
    private void removeBitmap(String key){
        BitmapWrapper wrapper=mPool.remove(key);

        if(wrapper!=null){
            Bitmap bitmap=wrapper.bitmap;
            if(bitmap!=null){
                mPoolRemain+=bitmap.getByteCount();
                bitmap.recycle();
                System.out.println("释放一个Bitmap 剩余Bitmap池容量:"+(mPoolRemain/1024)+"KB");
            }
        }
    }

    /**
     * 寻找到引用最少的图片key.后面改为最近最少使用
     * @param map 图片引用
     * @return 图片key
     */
    private String findLeastReferKey(Map<String,List<ImageView>> map){
        Pair<String,Integer> pair=Pair.create("",0);

        for(Map.Entry<String,List<ImageView>> entry:map.entrySet()){
            List<ImageView> imageViewList=entry.getValue();

            int imageViewSize=imageViewList==null?0:imageViewList.size();
            if(imageViewSize==0) return entry.getKey();
            if(pair.second>imageViewSize)
                pair=Pair.create(entry.getKey(),imageViewSize);
        }
        return pair.first;
    }
}