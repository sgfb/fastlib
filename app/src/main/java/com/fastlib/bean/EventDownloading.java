package com.fastlib.bean;

/**
 * Created by sgfb on 16/9/20.
 * 文件下载时发送的进度广播
 */
public class EventDownloading{
    public long maxLength;
    public int speed; // 速率/秒
    public String path;

    public EventDownloading(long maxLength,int speed,String path){
        this.maxLength=maxLength;
        this.speed=speed;
        this.path=path;
    }
}
