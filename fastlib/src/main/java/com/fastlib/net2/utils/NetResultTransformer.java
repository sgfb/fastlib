package com.fastlib.net2.utils;

/**
 * Created by sgfb on 2020\02\28.
 * 处理网络请求返回中间转换
 */
public interface NetResultTransformer<T>{

    Object transform(T result);
}
