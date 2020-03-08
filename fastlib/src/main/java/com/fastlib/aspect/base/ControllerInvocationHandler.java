package com.fastlib.aspect.base;

import android.os.Handler;
import android.os.Looper;

import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.aspect.AspectManager;
import com.fastlib.aspect.ResultWrapper;
import com.fastlib.aspect.ThreadOn;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
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
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    @Override
    public Object intercept(final Object o, final Object[] objects, final MethodProxy methodProxy) throws Exception {
        if(methodProxy.getOriginalMethod().getAnnotations()==null||methodProxy.getOriginalMethod().getAnnotations().length==0)
            return methodProxy.invokeSuper(o,objects);

        final List<Annotation> annotations = new ArrayList<>();
        final ResultWrapper resultWrapper=new ResultWrapper();
        boolean isRunningOnOtherThread=false;
        ThreadOn threadOn = methodProxy.getOriginalMethod().getAnnotation(ThreadOn.class);    //特殊注解,未来可能拆分MethodInterceptor来实现特殊注解.

        flatAnnotation(annotations,Arrays.asList(methodProxy.getOriginalMethod().getAnnotations()));
        if(threadOn==null){
            for(Annotation annotation:annotations){
                if(annotation.annotationType()==ThreadOn.class){
                    threadOn= (ThreadOn) annotation;
                    break;
                }
            }
        }
        if (threadOn != null) {
            annotations.remove(threadOn);
            //下面两种情况符合切换线程环境条件.但是不能锁主线程所以在主线程中调用子线程环境方法返回一定是空的
            if (threadOn.value() == ThreadOn.ThreadType.MAIN &&
                    Thread.currentThread() != Looper.getMainLooper().getThread()) {
                isRunningOnOtherThread=true;
                mMainHandler.post(new Runnable() {
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

    /**
     * 平铺注解.将注解的注解拿出来填充到注解的前面
     */
    private void flatAnnotation(List<Annotation> flatList, List<Annotation> list) {
        AspectManager am=AspectManager.getInstance();
        for (Annotation annotation : list) {
            if (flatList.contains(annotation) ||!am.checkAnnotationIsAction(annotation.annotationType()))
                continue;
            flatList.add(0, annotation);
            if (!list.isEmpty())
                flatAnnotation(flatList, Arrays.asList(annotation.annotationType().getAnnotations()));
        }
    }

    private Object intercept(List<Annotation> annotations, final Object o, final Object[] objects, MethodProxy methodProxy) {
        if (annotations != null && !annotations.isEmpty()) {
            AspectManager am = AspectManager.getInstance();

            //准备运行时环境参数
            List runtimeEnvs=AspectManager.getInstance().getRuntimeEnvs(o);
            return am.callAction(o, annotations, runtimeEnvs, objects, methodProxy);
        } else return methodProxy.invokeSuper(o, objects);
    }
}
