package com.fastlib.url_image.pool;

import android.graphics.Bitmap;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.fastlib.db.SaveUtil;
import com.fastlib.url_image.CallbackParcel;
import com.fastlib.url_image.bean.BitmapWrapper;

import java.io.File;
import java.io.IOException;
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

    BitmapPool(TargetReference imageViewReference){
        mPool=new HashMap<>();
        mImageViewReference = imageViewReference;

        mPoolSize=mPoolRemain=getMemoryClass()*1024*1024/5;  //可用内存的5分之1
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
        mPoolRemain-=bitmap==null?wrapper.compressData.length:bitmap.getByteCount();
        System.out.println("Bitmap入池 申请"+(bitmap==null?wrapper.compressData.length/1024:bitmap.getByteCount()/1024)+" 剩余池容量:"+(mPoolRemain/1024)+"KB");
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
        Map<String,List<CallbackParcel>> reference= mImageViewReference.getReference();
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
    private String findLeastReferKey(Map<String,List<CallbackParcel>> map){
        Pair<String,Integer> pair=Pair.create("",0);

        for(Map.Entry<String,List<CallbackParcel>> entry:map.entrySet()){
            List<CallbackParcel> imageViewList=entry.getValue();

            int imageViewSize=imageViewList==null?0:imageViewList.size();
            if(imageViewSize==0) return entry.getKey();
            if(pair.second>imageViewSize)
                pair=Pair.create(entry.getKey(),imageViewSize);
        }
        return pair.first;
    }

    /**
     * 绕过ActivityManager获取每个App能使用的最大内存(未开启large heap)
     * @return 单app使用最大内存，MB单位
     */
    private int getMemoryClass(){

        final int fDefaultLimit=30;
        try {
            File file=new File("/system/build.prop");

            if(!file.exists()) return fDefaultLimit;
            final String fHeapLimit="dalvik.vm.heapgrowthlimit=";
            String prop= new String(SaveUtil.loadFile(file.getAbsolutePath()));
            int heaplimitIndex=prop.indexOf(fHeapLimit);
            int heaplimitLineLastIndex=prop.indexOf("\n",heaplimitIndex);
            if(heaplimitIndex!=-1&&heaplimitLineLastIndex!=-1){
                String heaplimitStr=prop.substring(heaplimitIndex+fHeapLimit.length(),heaplimitLineLastIndex);
                if(!TextUtils.isEmpty(heaplimitStr)&&heaplimitStr.length()>1)
                    return Integer.parseInt(heaplimitStr.substring(0,heaplimitStr.length()-1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fDefaultLimit;
    }
}