package com.fastlib.test.UrlImage.request;

import android.app.Activity;
import android.support.v4.app.Fragment;

import java.io.File;

/**
 * Created by sgfb on 18/1/23.
 * 图像请求工厂
 */
public class BitmapRequestFactory{
    private Object mHost;

    public BitmapRequestFactory(Activity activity){
        mHost=activity;
    }

    public BitmapRequestFactory(Fragment fragment){
        mHost=fragment;
    }

    /**
     * 工厂建造一个url图像请求
     * @param url 远程服务器地址
     * @return url图像请求
     */
    public UrlBitmapRequest bitmapRequestByUrl(String url){
        if(mHost instanceof Activity) return new UrlBitmapRequest(url,(Activity)mHost);
        return new UrlBitmapRequest(url,(Fragment)mHost);
    }

    /**
     * 工厂建造一个来自磁盘的图像请求
     * @param file 文件路径
     * @return 磁盘图像请求
     */
    public DiskBitmapRequest bitmapRequestByDisk(File file){
        if(mHost instanceof Activity) return new DiskBitmapRequest(file,(Activity)mHost);
        return new DiskBitmapRequest(file,(Fragment)mHost);
    }

    /**
     * 工厂建造一个来自资源库的图像请求
     * @param resourceId 资源id
     * @return 资源库图像请求
     */
    public ResourceBitmapRequest bitmapRequestResource(int resourceId){
        if(mHost instanceof Activity) return new ResourceBitmapRequest(resourceId,(Activity)mHost);
        return new ResourceBitmapRequest(resourceId,(Fragment)mHost);
    }
}