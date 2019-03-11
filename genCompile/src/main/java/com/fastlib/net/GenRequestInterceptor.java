package com.fastlib.net;

/**
 * 生成Request时的拦截器.用于调整生成的Request
 * @param <T> 默认Request
 */
public interface GenRequestInterceptor<T>{

    /**
     * 自动生成前回调
     * @param request 自动生成的request
     */
    void genCompleteBefore(T request);
}
