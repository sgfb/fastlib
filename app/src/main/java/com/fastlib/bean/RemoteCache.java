package com.fastlib.bean;

import com.fastlib.annotation.Database;

/**
 * 包裹从网络中获取的缓存.这个缓存将会经过数据库
 */
public class RemoteCache {
    @Database(keyPrimary = true)
    private String cacheName;
    //缓存数据会被转换成json，以便存储和传输
    private String cache;

    public String getCache() {
        return cache;
    }

    public void setCache(String cache) {
        this.cache = cache;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }
}