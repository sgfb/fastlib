package com.fastlib;

import android.os.MemoryFile;
import android.text.format.Formatter;
import android.util.LongSparseArray;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 内存池
 * 提供内存级存储{@link android.os.MemoryFile}
 */
public final class MemoryPool {
    private long mMaxLimit;
    private long mCursor;
    private Map<String,MemoryFile> mMemoryFiles=new HashMap<>();
    private static MemoryPool mOwner;

    public synchronized static MemoryPool getInstance(){
        if(mOwner==null) mOwner=new MemoryPool();
        return mOwner;
    }

    private MemoryPool(){
        mMaxLimit=Runtime.getRuntime().maxMemory();
    }

    public boolean cacheExists(String name){
        return mMemoryFiles.containsKey(name);
    }

    public byte[] getCache(String name){
        MemoryFile mf=mMemoryFiles.get(name);
        if(mf==null) return null;

        byte[] data=new byte[mf.length()];
        try {
            mf.readBytes(data,0,0,mf.length());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return data;
    }

    public boolean putCache(String name,byte[] data) {
        removeCache(name);
        if(mCursor+data.length>mMaxLimit) return false;
        try {
            MemoryFile mf=new MemoryFile(String.format(Locale.getDefault(),"image-%s",name),data.length);
            mf.writeBytes(data,0,0,data.length);
            mCursor+=data.length;
            mMemoryFiles.put(name,mf);
            showUsageChanged(mf.length());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean removeCache(String name){
        MemoryFile mf=mMemoryFiles.remove(name);
        if(mf==null) return false;

        int length=mf.length();
        mf.close();

        mCursor-=length;
        showUsageChanged(-length);
        return true;
    }

    private void showUsageChanged(int changeValue){
        String sign=changeValue>0?"up":"down";
        String changeValueFormat="B";
        String poolCurrFormat="B";
        String maxFormat="B";
        if(changeValue>=1024){
            changeValue/=1024;
            changeValueFormat="KB";
        }
        if (changeValue >=1024) {
            changeValue/=1024;
            changeValueFormat="MB";
        }

        long cursor=mCursor;
        if(cursor>=1024){
            cursor/=1024;
            poolCurrFormat="KB";
        }
        if(cursor>=1024){
            cursor/=1024;
            poolCurrFormat="MB";
        }

        long maxLimit=mMaxLimit;
        if(maxLimit>=1024){
            maxLimit/=1024;
            maxFormat="KB";
        }
        if(maxLimit>=1024){
            maxLimit/=1024;
            maxFormat="MB";
        }
        System.out.println(String.format(Locale.getDefault(),"cache pool:%s %s%s,curr %s%s,max %s%s",
                sign,changeValue,changeValueFormat,cursor,poolCurrFormat,maxLimit,maxFormat));
    }
}