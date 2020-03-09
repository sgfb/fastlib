package com.fastlib.url_image.request;

import android.app.Activity;
import android.content.Context;
import androidx.fragment.app.Fragment;

import java.io.File;

/**
 * Created by sgfb on 18/1/23.
 * 图像请求工厂
 */
public class RequestFactory {
    private Object mHost;

    private RequestFactory(Activity activity){
        mHost=activity;
    }

    private RequestFactory(Fragment fragment){
        mHost=fragment;
    }

    public static RequestFactory host(Context context){
        if(context instanceof Activity)
            return host((Activity) context);
        else throw new IllegalArgumentException("this context not instanceof activity");
    }

    public static RequestFactory host(Activity activity){
        return new RequestFactory(activity);
    }

    public static RequestFactory host(Fragment fragment){
        return new RequestFactory(fragment);
    }

    /**
     * 工厂建造一个url图像请求
     * @param url 远程服务器地址
     * @return url图像请求
     */
    public UrlImageRequest byUrl(String url){
        if(mHost instanceof Activity) return new UrlImageRequest(url,(Activity)mHost);
        return new UrlImageRequest(url,(Fragment)mHost);
    }

    /**
     * 工厂建造一个来自磁盘的图像请求
     * @param file 文件路径
     * @return 磁盘图像请求
     */
    public DiskImageRequest byDisk(File file){
        if(mHost instanceof Activity) return new DiskImageRequest(file,(Activity)mHost);
        return new DiskImageRequest(file,(Fragment)mHost);
    }

    /**
     * 工厂建造一个来自资源库的图像请求
     * @param resourceId 资源id
     * @return 资源库图像请求
     */
    public ResourceImageRequest byResource(int resourceId){
        if(mHost instanceof Activity) return new ResourceImageRequest(resourceId,(Activity)mHost);
        return new ResourceImageRequest(resourceId,(Fragment)mHost);
    }
}