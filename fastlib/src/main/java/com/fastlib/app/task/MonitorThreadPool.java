package com.fastlib.app.task;

import android.util.SparseIntArray;
import android.util.SparseLongArray;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 监听控制线程池
 */
public class MonitorThreadPool extends ThreadPoolExecutor{
    public static final int THREAD_STATUS_IDLE =1;
    public static final int THREAD_STATUS_RUNNING=2;
    public static final int THREAD_STATUS_SLEEP=3;

    private OnThreadStatusChangedListener mListener;
    private SparseIntArray mRunnableThread =new SparseIntArray();
    private SparseIntArray mThreadIndex =new SparseIntArray();
    private ConcurrentHashMap<Integer,Long> mStartTimeMap=new ConcurrentHashMap<>();
    private long mConsumeCount;

    public MonitorThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public MonitorThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public MonitorThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public MonitorThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r){
        int tHash=t.hashCode();
        int index=mThreadIndex.get(tHash);

        if(mThreadIndex.get(tHash)==0)
            mThreadIndex.put(tHash,index=mThreadIndex.size()+1);
        mRunnableThread.put(r.hashCode(),tHash);
        if(mListener!=null)
            mListener.onThreadStatusChanged(index,THREAD_STATUS_RUNNING);
        mStartTimeMap.put(tHash,System.currentTimeMillis());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t){
        int rHash=r.hashCode();
        int tHash=mRunnableThread.get(rHash);

        if(mListener!=null&&tHash!=0){
            mRunnableThread.removeAt(mRunnableThread.indexOfKey(rHash));

            int index=mThreadIndex.get(tHash);
            if(index!=0) mListener.onThreadStatusChanged(index,THREAD_STATUS_IDLE);
        }
        mConsumeCount+=(System.currentTimeMillis()-mStartTimeMap.remove(tHash));
    }

    public void setThreadStatusChangedListener(OnThreadStatusChangedListener listener){
        mListener=listener;
    }

    public interface OnThreadStatusChangedListener {
        void onThreadStatusChanged(int position,int status);
    }
}