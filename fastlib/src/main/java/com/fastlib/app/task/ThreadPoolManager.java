package com.fastlib.app.task;

import android.util.Log;

import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by sgfb on 2019\03\10.
 * 线程管理
 */
public class ThreadPoolManager {
    private static final String TAG=ThreadPoolManager.class.getCanonicalName();
    public final static ThreadPoolExecutor sQuickPool; //轻请求线程池 理论上任务应小于100ms
    public final static ThreadPoolExecutor sSlowPool;  //重请求线程池 适用io、网络等延迟比较大的任务

    static{
        int quickPoolCount=Math.max(2,Runtime.getRuntime().availableProcessors()/2);
        sQuickPool=new MonitorThreadPool(quickPoolCount,quickPoolCount,0L,TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());

        int slowPoolCount=Runtime.getRuntime().availableProcessors()+2;
        sSlowPool=new MonitorThreadPool(slowPoolCount,slowPoolCount,0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
        Log.d(TAG,String.format(Locale.getDefault(),"init poos quickPool:%d slowPool:%d",quickPoolCount,slowPoolCount));
    }

    public static void setOnThreadChanageListener(final MonitorThreadPool.OnThreadStatusChangedListener listener){
        ((MonitorThreadPool)sQuickPool).setThreadStatusChangedListener(listener);
        ((MonitorThreadPool)sSlowPool).setThreadStatusChangedListener(new MonitorThreadPool.OnThreadStatusChangedListener() {
            @Override
            public void onThreadStatusChanged(int position, int status) {
                if(listener!=null) listener.onThreadStatusChanged(position+sQuickPool.getPoolSize()+position,status);
            }
        });
    }

    public static int getThreadCount(){
        return sQuickPool.getMaximumPoolSize()+sSlowPool.getMaximumPoolSize();
    }

    public static long getCompleteTaskCount(){
        return sQuickPool.getCompletedTaskCount()+sSlowPool.getCompletedTaskCount();
    }
}
