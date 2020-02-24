package com.fastlib.aspect;

import android.os.Handler;
import android.os.Looper;

import leo.android.cglib.proxy.MethodInterceptor;
import leo.android.cglib.proxy.MethodProxy;

/**
 * Created by sgfb on 2020\02\24.
 * 拦截所有方法置于主线程内调用
 * 如果调用方在子线程中且有返回则加锁到当前方法执行完后返回
 */
public class MainThreadInvocation implements MethodInterceptor{
    private Handler mMainHandler=new Handler(Looper.getMainLooper());
    private Object mResult;

    @Override
    public Object intercept(final Object o, final Object[] objects, final MethodProxy methodProxy) throws Exception{
        boolean currentMainThread=Thread.currentThread()==Looper.getMainLooper().getThread();
        if(currentMainThread)
            return methodProxy.invokeSuper(o,objects);
        else {
            final boolean needResult=methodProxy.getOriginalMethod().getReturnType()!=void.class;
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(needResult){
                        mResult=methodProxy.invokeSuper(o,objects);
                        synchronized (MainThreadInvocation.class){
                            MainThreadInvocation.this.notify();
                        }
                    }
                    else methodProxy.invokeSuper(o,objects);
                }
            });
            if(needResult){
                synchronized (MainThreadInvocation.class){
                    MainThreadInvocation.this.wait();
                }
            }
        }
        return mResult;
    }
}
