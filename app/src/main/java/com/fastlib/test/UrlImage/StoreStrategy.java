package com.fastlib.test.UrlImage;

/**
 * Created by sgfb on 2017/11/4.
 * 图像存储策略，可以用在整个Url图像模块中也可以对某个独立图像请求使用
 */
public enum StoreStrategy{
    DEFAULT, //默认内存，外存 二级缓存
    NO_MEMORY, //不保存至内存
    NO_SAVE //一定从服务器中取，即使下载了也被打上标记会被最优先清理
}