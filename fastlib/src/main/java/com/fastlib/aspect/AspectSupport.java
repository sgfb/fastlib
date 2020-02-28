package com.fastlib.aspect;

import android.os.Handler;
import android.os.Looper;

import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.aspect.exception.ExceptionHandler;

import java.lang.reflect.Method;

/**
 * Created by sgfb on 2020\02\23.
 * 对Activity等无法使用代理类支持切面需要一些额外的控制
 */
public class AspectSupport {

    private AspectSupport(){}

    public static void callMethod(final Object host, final Method method, final Object... args){
        ThreadOn threadOn=method.getAnnotation(ThreadOn.class);
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                try{
                    method.invoke(host,args);
                }catch (Exception e){
                    handleException(host,e);
                }
            }
        };

        if(threadOn!=null){
            ThreadOn.ThreadType threadType=threadOn.value();
            boolean currentMainThread=Thread.currentThread()==Looper.getMainLooper().getThread();

            if(threadType==ThreadOn.ThreadType.MAIN&&!currentMainThread) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(runnable);
            }
            else if(threadType==ThreadOn.ThreadType.WORK&&currentMainThread){
                if(threadOn.weight()==ThreadOn.ThreadWeight.HEAVY)
                    ThreadPoolManager.sSlowPool.execute(runnable);
                else if(threadOn.weight()==ThreadOn.ThreadWeight.LIGHT)
                    ThreadPoolManager.sQuickPool.execute(runnable);
            }
            else runnable.run();
        }
        else runnable.run();
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