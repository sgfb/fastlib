package com.fastlib.image_manager.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v4.util.Pair;
import android.util.SparseArray;

import com.fastlib.db.MemoryPool;
import com.fastlib.image_manager.ImageUtils;
import com.fastlib.image_manager.request.ImageRequest;
import com.fastlib.utils.ScreenUtils;

import java.io.ByteArrayOutputStream;

/**
 * Created by sgfb on 2019\02\24.
 * 内存池额外存储图像宽高元数据
 */
public class ImageMemoryPool{
    private static ImageMemoryPool mInstance;
    private SparseArray<Point> mSizeMap=new SparseArray<>();

    private ImageMemoryPool(){

    }

    public synchronized static ImageMemoryPool getInstance(){
        if(mInstance==null) mInstance=new ImageMemoryPool();
        return mInstance;
    }

    public boolean cacheExists(String name){
        return MemoryPool.getInstance().cacheExists(name);
    }

    public byte[] getCache(ImageRequest request,int oriWidth,int oriHeight){
        Point size=mSizeMap.get(request.hashCode());

        if(size!=null){
            int width=request.getRequestWidth();
            int height=request.getRequestHeight();
            if(width==0||height==0){
                Pair<Integer,Integer> screenSize=ScreenUtils.getScreenSize();
                return size.x<=screenSize.first&&size.y<=screenSize.second?MemoryPool.getInstance().getCache(request.getName()):null;
            }
            else if(width==-1&&height==-1){
                return oriWidth<=size.x&&oriHeight<=size.y?MemoryPool.getInstance().getCache(request.getName()):null;
            }
            else if(size.x>=width&&size.y>=height)
                return MemoryPool.getInstance().getCache(request.getName());
        }
        return null;
    }

    public boolean putCache(ImageRequest request,byte[] data){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeByteArray(data,0,data.length,options);

        int width=request.getRequestWidth();
        int height=request.getRequestHeight();
        byte[] saveData;
        if(width==-1&&height==-1){
            mSizeMap.put(request.hashCode(),new Point(options.outWidth,options.outHeight));
            saveData=data;
        }
        else if(width==0||height==0){
            Pair<Integer,Integer> screenSize=ScreenUtils.getScreenSize();
            if(width>screenSize.first||height>screenSize.second){
                //config 优化
                ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
                Bitmap bitmap=ImageUtils.cropBitmap(screenSize.first,screenSize.second,request.getBitmapConfig(),data);
                bitmap.compress(Bitmap.CompressFormat.WEBP,90,outputStream);
                saveData=outputStream.toByteArray();
            }
            else{
                mSizeMap.put(request.hashCode(),new Point(options.outWidth,options.outHeight));
            }
        }
        else{
            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
            Bitmap bitmap=ImageUtils.cropBitmap(width,height,request.getBitmapConfig(),data);
            bitmap.compress(Bitmap.CompressFormat.WEBP,90,outputStream);
            saveData=outputStream.toByteArray();
        }
        return MemoryPool.getInstance().putCache(request.getName(),saveData);
    }
}