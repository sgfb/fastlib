package com.fastlib.bean;

/**
 * Created by sgfb on 16/9/20.
 * 文件下载时发送的进度广播
 */
public class EventDownloading{
    private long mMaxLength;
    private int mSpeed; //字节/秒
    private String mPath;

    public EventDownloading(long maxLength,int speed,String path){
        this.mMaxLength =maxLength;
        this.mSpeed =speed;
        this.mPath =path;
    }

    public long getMaxLength() {
        return mMaxLength;
    }

    public int getSpeed() {
        return mSpeed;
    }

    public String getPath() {
        return mPath;
    }
}
