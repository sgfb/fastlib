package com.fastlib.url_image.pool;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.Pair;

import com.fastlib.url_image.Target;
import com.fastlib.url_image.bean.BitmapWrapper;

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
    private Map<String,BitmapWrapper> mPool; //键自定义key 值Bitmap包裹
    private TargetReference mImageViewReference;

    BitmapPool(Context context,TargetReference imageViewReference){
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        mPool=new HashMap<>();
        mImageViewReference = imageViewReference;

        mPoolSize=mPoolRemain=am.getMemoryClass()*1024*1024/5;  //可用内存的5分之1
        System.out.println("生成Bitmap池 池总容量:"+(mPoolSize/1024)+"KB");
    }

    /**
     * 设置池大小,字节单位
     * @param poolSize 池最大容量
     */
    public void setPoolSize(long poolSize){
        if(poolSize<=0) throw new IllegalArgumentException("pool size must bigger than 0");
        long usePoolSize=mPoolSize-mPoolRemain;
        boolean popValid=true; //移出image动作是否有效
        while(usePoolSize>=poolSize&&popValid) {
            popValid = pop();
            usePoolSize=mPoolSize-mPoolRemain;
        }
        mPoolSize=poolSize;
        mPoolRemain=mPoolSize-usePoolSize;
        System.out.println("修改池大小:"+mPoolSize/1024+"KB");
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

    public long getRemainSize(){
        return mPoolRemain;
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
     * @return true移除了一个Bitmap,false无效果
     */
    private boolean pop(){
        Map<String,List<Target>> reference= mImageViewReference.getReference();
        if(!reference.isEmpty()){
            String leastReferKey=findLeastReferKey(reference);
            return removeBitmap(leastReferKey);
        }
        else if(!mPool.isEmpty()){ //独立BitmapPool判断
            return removeBitmap(mPool.entrySet().iterator().next().getKey());
        }
        return false;
    }

    /**
     * 移除一个Bitmap出池
     * @param key 对应键
     * @return 成功移除一个Bitmap
     */
    private boolean removeBitmap(String key){
        BitmapWrapper wrapper=mPool.remove(key);

        if(wrapper!=null){
            Bitmap bitmap=wrapper.bitmap;
            if(bitmap!=null){
                mPoolRemain+=bitmap.getByteCount();
                bitmap.recycle();
                System.out.println("释放一个Bitmap 剩余Bitmap池容量:"+(mPoolRemain/1024)+"KB");
                return true;
            }
        }
        return false;
    }

    /**
     * 寻找到引用最少的图片key.后面改为最近最少使用
     * @param map 图片引用
     * @return 图片key
     */
    private String findLeastReferKey(Map<String,List<Target>> map){
        Pair<String,Integer> pair=Pair.create("",0);

        for(Map.Entry<String,List<Target>> entry:map.entrySet()){
            List<Target> imageViewList=entry.getValue();

            int imageViewSize=imageViewList==null?0:imageViewList.size();
            if(imageViewSize==0) return entry.getKey();
            if(pair.second>imageViewSize)
                pair=Pair.create(entry.getKey(),imageViewSize);
        }
        return pair.first;
    }
}