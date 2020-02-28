package com.fastlib.aspect.component;

import com.fastlib.aspect.StaticProvier;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sgfb on 2020\02\19.
 * 简单保存缓存.永不过期
 */
@StaticProvier
public class SimpleAspectCacheManager implements AspectCache {
    private Map<String,Object> mCaches=new HashMap<>();

    @Override
    public void saveCache(String name, Object cache) {
        mCaches.put(name,cache);
    }

    @Override
    public Object getCache(String name) {
        return mCaches.get(name);
    }

    @Override
    public boolean checkExpire() {
        return false;
    }
}
