package com.fastlib;

import android.os.MemoryFile;
import android.util.LongSparseArray;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 图像池
 * 1.提供内存级存储{@link android.os.MemoryFile}
 * 3.当空间不足时自动移除不需要的资源
 * 4.插入图像
 */
public final class ImagePool{
    private long mMaxLimit;
    private long mCursor;
    private Map<String,MemoryFile> mMemoryFiles=new HashMap<>();
    private ImageRef mImageRef;

    ImagePool(){
        mMaxLimit=Runtime.getRuntime().maxMemory();
    }

    public byte[] getImage(String name){
        MemoryFile mf=mMemoryFiles.get(name);
        byte[] data=new byte[mf.length()];
        try {
            mf.readBytes(data,0,0,mf.length());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return data;
    }

    public boolean putImage(String name,byte[] data) {
        if(mCursor+data.length>mMaxLimit) return false;
        try {
            MemoryFile mf=new MemoryFile(String.format(Locale.getDefault(),"image-%s",name),data.length);
            mf.readBytes(data,0,0,data.length);
            mCursor+=data.length;

            float mb=(float) mCursor/1024/1024;
            DecimalFormat df=new DecimalFormat("##.##");
            System.out.println("image pool:"+df.format(mb)+"mb");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 移除不必要缓存
     * 先移除引用少，后移除老的数据
     */
    private boolean removeRedundant(){
        for(Map.Entry<String,MemoryFile> entry:mMemoryFiles.entrySet()){
            MemoryFile mf=mMemoryFiles.remove(entry.getKey());
            mCursor-=mf.length();
            mf.close();
        }
        return true;
    }
}