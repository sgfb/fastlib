package com.fastlib.aspect;

import android.os.Handler;
import android.os.Looper;

import com.fastlib.app.task.ThreadPoolManager;

import java.lang.reflect.Method;

/**
 * Created by sgfb on 2020\01\10.
 * 切面线程环境切换功能
 */
public class ThreadInvocationHandler extends InvocationHandlerNode<ThreadOn>{
    private static final Object sLock=new Object();

    public ThreadInvocationHandler(ThreadOn mAnnotation, InvocationHandlerNode mNext) {
        super(mAnnotation, mNext);
    }

    @Override
    protected void handle(Object host,Class proxyClass, Method proxyMethod) throws Exception {
        //切换线程环境.但是不能锁主线程所以在主线程中调用子线程环境方法返回一定是空的
        switch (mAnnotation.value()){
            case MAIN:{
                if(Thread.currentThread()==Looper.getMainLooper().getThread())
                    startNext(null);
                else{
                    Handler handler=new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            startNext(null);
                            synchronized (sLock) {
                                sLock.notify();
                            }
                        }
                    });
                    synchronized (sLock){
                        sLock.wait();
                    }
                }
            }
            case WORK:{
                if(Thread.currentThread()==Looper.getMainLooper().getThread()){
                    ThreadPoolManager.sSlowPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            startNext(null);
                        }
                    });
                }
                else startNext(null);
            }
        }
    }
}
