package com.fastlib.aspect;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.aspect.exception.ExceptionHandler;
import com.fastlib.app.AsyncCallback;

import java.lang.reflect.Method;

/**
 * Created by sgfb on 2020\02\23.
 * 对Activity等无法使用代理类支持切面需要一些额外的控制
 */
public class AspectSupport {

    private AspectSupport(){}

    public static Object callMethod(final Object host, final Method method, final Object... args){
        return callMethod(host,method,null,args);
    }

    /**
     * @param callback 异步回调支持.触发异步回调前如果是主线程那么触发时也是主线程
     * @return 如果切换线程环境并且没有指定返回必定空.可以使用 {@link AsyncCallback}来支持异步返回
     */
    public static Object callMethod(final Object host, final Method method, @Nullable final AsyncCallback callback, final Object... args){
        final boolean runningMainThread=Thread.currentThread()==Looper.getMainLooper().getThread();
        ThreadOn threadOn=method.getAnnotation(ThreadOn.class);
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                try{
                    final Object result=method.invoke(host,args);
                    if(callback!=null){
                        boolean currRunningMainThread=Thread.currentThread()==Looper.getMainLooper().getThread();
                        if(runningMainThread&&!currRunningMainThread){
                            ThreadPoolManager.getMainHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.callback(result);
                                }
                            });
                        }
                        else callback.callback(result);
                    }
                }catch (Exception e){
                    handleException(host,e);
                }
            }
        };

        try{
            if(threadOn!=null){
                ThreadOn.ThreadType threadType=threadOn.value();
                boolean currentMainThread=Thread.currentThread()==Looper.getMainLooper().getThread();

                if(threadType==ThreadOn.ThreadType.MAIN&&!currentMainThread) {
                    ThreadPoolManager.getMainHandler().post(runnable);
                }
                else if(threadType==ThreadOn.ThreadType.WORK&&currentMainThread){
                    if(threadOn.weight()==ThreadOn.ThreadWeight.HEAVY)
                        ThreadPoolManager.sSlowPool.execute(runnable);
                    else if(threadOn.weight()==ThreadOn.ThreadWeight.LIGHT)
                        ThreadPoolManager.sQuickPool.execute(runnable);
                }
                else return method.invoke(host,args);
            }
            else{
                return method.invoke(host,args);
            }
        }catch (Exception e) {
            handleException(host,e);
        }
        return null;
    }

    /**
     * 如果宿主支持,处理错误
     * @param host  支持切面的对象
     * @param e     所有在切面调用时的错误
     */
    public static void handleException(Object host,Exception e){
        if(host instanceof ExceptionHandler){
            ExceptionHandler exceptionHandler= (ExceptionHandler) host;
            exceptionHandler.onException(e);
        }
    }
}