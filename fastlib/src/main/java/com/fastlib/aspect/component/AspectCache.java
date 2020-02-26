package com.fastlib.aspect.component;

/**
 * Created by sgfb on 2020\02\19.
 * 切面缓存实现
 */
public interface AspectCache{

    void saveCache(String name,Object cache);

    Object getCache(String name);

    /**
     * 过时策略
     * @return  true已过时,将会调用原方法并且保存 false未过时将不调起原方法直接返回缓存
     */
    boolean checkExpire();
}
