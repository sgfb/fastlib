package com.fastlib.net;

/**
 * 生成Request时的拦截器.用于调整生成的Request
 * @param <T> 默认Request
 */
public interface GenRequestInterceptor<T>{

    void genCompleteBefore(T request);
}
