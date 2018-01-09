package com.fastlib.test.UrlImage;

import java.io.File;

/**
 * Created by sgfb on 2017/11/5.
 * 配置清单
 */
public class FastImageConfig implements Cloneable{

    /**
     * 单请求配置
     */
    //加载策略
    public static final int STORE_STRATEGY_DEFAULT=1;  //默认磁盘内存缓存
    public static final int STORE_STRATEGY_NO_MEMORY=2;  //不留存内存池
    public static final int STORE_STRATEGY_NO_SAVE=3;  //不留存内存池并且优先从磁盘中删除

    /**
     * 全局配置
     */
    public File mSaveFolder;

    @Override
    protected FastImageConfig clone() throws CloneNotSupportedException{
        return (FastImageConfig) super.clone();
    }
}