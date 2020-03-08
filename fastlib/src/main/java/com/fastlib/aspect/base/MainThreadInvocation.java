package com.fastlib.aspect.base;

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
                    //这行代码错开直接判断防止这里的代码执行的比工作线程快
                    Object result=methodProxy.invokeSuper(o,objects);
                    if(needResult){
                        mResult=result;
                        synchronized (MainThreadInvocation.class){
                            MainThreadInvocation.class.notify();
                        }
                    }
                    else methodProxy.invokeSuper(o,objects);
                }
            });
            if(needResult){
                synchronized (MainThreadInvocation.class){
                    MainThreadInvocation.class.wait();
                }
            }
        }
        return mResult;
    }
}
