package com.fastlib.aspect.component;

import com.fastlib.aspect.AspectAction;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by sgfb on 2020\03\08.
 */
public abstract class SimpleAspectAction<T extends Annotation> extends AspectAction<T>{
    private CrossLock mLock;

    protected abstract void handle(T anno,Method method,Object[] args);

    @Override
    protected void handleAction(T anno, Method method, Object[] args) {
        Field[] fields=getClass().getDeclaredFields();

        for(Field field:fields){
            AspectRuntimeArg runtimeArg=field.getAnnotation(AspectRuntimeArg.class);
            if(runtimeArg!=null){
                try {
                    field.setAccessible(true);
                    field.set(this,getEnv(runtimeArg.value()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        handle(anno,method,args);
    }

    protected void lock(){
        if(mLock==null) mLock=obtainLock();
        mLock.lock();
    }

    protected void unlock(){
        if(mLock==null) throw new IllegalStateException("未上锁却调起了解锁");
        mLock.unlock();
    }

    protected int getLockId(){
        if(mLock==null) mLock=obtainLock();
        return mLock.getId();
    }
}
