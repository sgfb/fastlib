package com.fastlib.image_manager.bean;

import android.os.Environment;

import java.io.File;

/**
 * Created by sgfb on 2017/11/5.
 * 配置清单
 */
public class ImageConfig implements Cloneable{

    /**
     * 单请求配置
     */
    //缓存策略 可多选
    public static final int STRATEGY_STORE_SAVE_MEMORY =1;  //缓存至内存
    public static final int STRATEGY_STORE_SAVE_DISK =1<<1; //缓存至磁盘
    //后续跟进压缩缓存策略

    //加载策略
    public static final int STRATEGY_LOAD_NORMAL=1;         //默认加载策略，过期判断
    public static final int STRATEGY_LOAD_NO_EXPIRE=2;      //永不过期.从远程服务器中取到后，永远不判断过期使用(仅适用远程图像请求)
    public static final int STRATEGY_LOAD_REMOTE_ONLY=3;    //一直是过期的.每次都请求远程服务器中的图像数据(仅适用远程图像请求)
    public static final int STRATEGY_LOAD_DISK_ONLY=4;      //仅加载磁盘中的数据，如果没有，仅调起错误回调

    /**
     * 全局配置
     */
    public File mSaveFolder= Environment.getExternalStorageDirectory();

    @Override
    public ImageConfig clone(){
        try {
            return (ImageConfig) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}