package com.fastlib.aspect.component;

/**
 * Created by sgfb on 2020\02\19.
 * 切面缓存实现
 */
public interface AspectCache{

    void saveCache(String name,Object cache);

    Object getCache(String name);

    final class AspectCacheDelegate{
        AspectCache aspectCache;

        public AspectCacheDelegate(AspectCache aspectCache) {
            this.aspectCache = aspectCache;
        }

        public AspectCache getAspectCache() {
            return aspectCache;
        }
    }
}
