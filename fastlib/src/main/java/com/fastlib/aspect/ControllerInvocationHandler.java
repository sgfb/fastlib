package com.fastlib.aspect;

import android.os.Handler;
import android.os.Looper;

import com.fastlib.app.task.ThreadPoolManager;
import com.google.common.collect.Lists;

import java.lang.annotation.Annotation;
import java.util.List;

import leo.android.cglib.proxy.MethodInterceptor;
import leo.android.cglib.proxy.MethodProxy;

/**
 * Created by sgfb on 2020\01\07.
 * MVC框架实现方法调用切面逻辑.控制器部分
 * 这个类是线程不安全的
 */
public class ControllerInvocationHandler implements MethodInterceptor {
    private static final Object sLock = new Object();

    @Override
    public Object intercept(final Object o, final Object[] objects, final MethodProxy methodProxy) throws Exception {
        if(methodProxy.getOriginalMethod().getAnnotations()==null||methodProxy.getOriginalMethod().getAnnotations().length==0)
            return methodProxy.invokeSuper(o,objects);

        final List<Annotation> annotations = Lists.newArrayList(methodProxy.getOriginalMethod().getAnnotations());
        boolean isRunningOnOtherThread=false;
        ThreadOn threadOn = methodProxy.getOriginalMethod().getAnnotation(ThreadOn.class);    //特殊注解,未来可能拆分MethodInterceptor来实现特殊注解
        final ResultWrapper resultWrapper=new ResultWrapper();

        if (threadOn != null) {
            annotations.remove(threadOn);
            //下面两种情况符合切换线程环境条件.但是不能锁主线程所以在主线程中调用子线程环境方法返回一定是空的
            if (threadOn.value() == ThreadOn.ThreadType.MAIN &&
                    Thread.currentThread() != Looper.getMainLooper().getThread()) {
                isRunningOnOtherThread=true;
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (sLock){
                            resultWrapper.result=intercept(annotations, o, objects, methodProxy);
                            sLock.notify();
                        }
                    }
                });
                synchronized (sLock) {
                    sLock.wait();
                }
            }
            if (threadOn.value() == ThreadOn.ThreadType.WORK &&
                    Thread.currentThread() == Looper.getMainLooper().getThread()) {
                isRunningOnOtherThread=true;
                ThreadPoolManager.sSlowPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        intercept(annotations, o, objects, methodProxy);
                    }
                });
            }
        }
         if(isRunningOnOtherThread)
            return resultWrapper.result;
        return intercept(annotations, o, objects, methodProxy);
    }

    private Object intercept(List<Annotation> annotations, final Object o, final Object[] objects, MethodProxy methodProxy) {
        if (annotations != null && !annotations.isEmpty()) {
            AspectManager am = AspectManager.getInstance();
            AspectEnvironmentProvider provider = null;
            if (o instanceof AspectEnvironmentProvider)
                provider = (AspectEnvironmentProvider) o;
            List envs = provider != null ? provider.getAspectEnvironment() : null;
            return am.callAction(o, annotations, envs, objects, methodProxy);
        } else return methodProxy.invokeSuper(o, objects);
    }
}
