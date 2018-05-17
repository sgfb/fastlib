package com.fastlib.url_image.pool;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.fastlib.url_image.Target;
import com.fastlib.url_image.bean.BitmapWrapper;
import com.fastlib.url_image.request.BitmapRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sgfb on 2017/11/4.
 * 管理内存中的ImageView对Bitmap引用
 */
public class TargetReference {
    private Map<String,List<Target>> mReference;
    private BitmapPool mBitmapPool;

    public TargetReference(Context context){
        mReference=new HashMap<>();
        mBitmapPool=new BitmapPool(context,this);
    }

    /**
     * 检测内存中是否有指定图像.如果请求的图像存在则判断
     * 如果是原图返回存在
     * 请求的尺寸大于存在内存中的图像判断为不存在，如果小于则判断为存在
     * @param request 图像请求
     * @return true存在内存中，false不存在内存中
     */
    public boolean checkContainImage(BitmapRequest request){
        if(mReference.containsKey(request.getKey())) {
            BitmapWrapper wrapper = mBitmapPool.getBitmapWrapper(request.getKey());

            if (wrapper == null) return false;
            return wrapper.sampleSize <= 1 || wrapper.sampleSize<=BitmapWrapper.getLocalImageScale(request.getRequestWidth(), request.getRequestHeight(), wrapper.originWidth, wrapper.originHeight);
        }
        return false;
    }

    /**
     * 从内存中获取图像
     * @param request 图像请求
     * @return 如果存在返回Bitmap否则返回null
     */
    public Bitmap getFromMemory(BitmapRequest request){
        if(checkContainImage(request)){
            BitmapWrapper wrapper=mBitmapPool.getBitmapWrapper(request.getKey());
            int requestWidth=request.getRequestWidth();
            int requestHeight=request.getRequestHeight();
            int bitmapWidth=wrapper.bitmap.getWidth();
            int bitmapHeight=wrapper.bitmap.getHeight();

            //图像一定大于等于请求尺寸，所以只判断是否需要返回缩小的图像(等比缩放)
            if(requestWidth!=0&&requestHeight!=0&&(requestWidth<bitmapWidth||requestHeight<bitmapHeight))
                return wrapper.getThumbBitmap(requestWidth,requestHeight);
            return wrapper.bitmap;
        }
        return null;
    }

    /**
     * 增加图像引用.间接将图像载入内存中持有
     * @param request 图像请求
     * @param wrapper 图像包裹
     * @param imageView 视图引用
     */
    public void addBitmapReference(BitmapRequest request,BitmapWrapper wrapper,Target imageView){
        if(wrapper==null||imageView==null) return;
        //先判断是否引用了其它的url再判断当前url的ImageView列表中是否存在
        for(Map.Entry<String,List<Target>> entry:mReference.entrySet()){
            List<Target> imageViewList=entry.getValue();
            if(imageViewList.contains(imageView)){
                imageViewList.remove(imageView);
                break;
            }
        }
        List<Target> list=mReference.get(request.getKey());

        if(list==null) {
            list=new Vector<>();
            mReference.put(request.getKey(),list);

        }
        if(!list.contains(imageView))
            list.add(imageView);
        mBitmapPool.addBitmap(request.getKey(),wrapper);  //不判断该url是否已有Bitmap在池中，因为服务器的图像资源也可能会被修改
    }

    /**
     * 清空所有引用和内存池中的图像
     */
    public void clear(){
        for(Map.Entry<String,List<Target>> entry:mReference.entrySet()){
            List<Target> list=entry.getValue();
            if(list!=null) list.clear();
        }
        mReference.clear();
        mBitmapPool.clear();
        System.gc();
        System.out.println("清空图像池");
    }

    public void remove(Target target){
        List<Target> targets=mReference.get(target.getKey());
        if(targets!=null) targets.remove(target);
    }

    public Map<String,List<Target>> getReference(){
        return mReference;
    }

    public BitmapPool getBitmapPool(){
        return mBitmapPool;
    }
}