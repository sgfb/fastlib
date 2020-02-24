package com.fastlib.aspect.component.opaque_action;

import com.fastlib.aspect.AspectAction;
import com.fastlib.aspect.MethodResultCallback;
import com.fastlib.aspect.component.AspectCache;
import com.fastlib.aspect.component.inject.Cache;

/**
 * Created by sgfb on 2020\02\19.
 */
public class CacheAction extends AspectAction<Cache> {

    @Override
    protected void handleAction(final Cache anno, Object[] args) {
        final AspectCache cacheManager=getEnv(AspectCache.class);

        Object cache=cacheManager.getCache(anno.value());
        if(cache==null){
            setActionCallback(new MethodResultCallback() {
                @Override
                public void onRawMethodResult(Object result) {
                    cacheManager.saveCache(anno.value(),result);
                }
            });
        }
        else setResult(cache);
        setPassed(true);
    }
}
