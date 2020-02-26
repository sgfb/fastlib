package com.fastlib.aspect.component.opaque_action;

import android.text.TextUtils;

import com.fastlib.aspect.AspectAction;
import com.fastlib.aspect.MethodResultCallback;
import com.fastlib.aspect.component.AspectCache;
import com.fastlib.aspect.component.inject.Cache;

import java.lang.reflect.Method;

/**
 * Created by sgfb on 2020\02\19.
 */
public class CacheAction extends AspectAction<Cache> {

    @Override
    protected void handleAction(final Cache anno, Method method,Object[] args) {
        final AspectCache cacheManager=getEnv(AspectCache.class);

        String key=!TextUtils.isEmpty(anno.value())?anno.value():method.getName();
        Object cache=cacheManager.getCache(key);
        if(cache==null||cacheManager.checkExpire()){
            setActionCallback(new MethodResultCallback() {
                @Override
                public void onRawMethodResult(Object result){
                    cacheManager.saveCache(anno.value(),result);
                }
            });
        }
        else setResult(cache);
        setPassed(true);
    }
}
