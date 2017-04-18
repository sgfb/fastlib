package com.fastlib.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by sgfb on 17/2/23.
 * 任务队列头
 */
public class TaskChainHead<T>{
    private T mData;

    private TaskChainHead(T data){
        mData=data;
    }

    public static <T> TaskChainHead<T> begin(T data){
        return new TaskChainHead<T>(data);
    }

    /**
     * 安排下个任务,默认子线程中
     * @param action
     * @param <R1>
     * @return
     */
    public <R1> TaskChain<T,R1> next(TaskAction<T,R1> action){
        return next(action,TaskChain.TYPE_THREAD_ON_WORK);
    }

    /**
     * 安排下个任务,指定线程位置
     * @param action
     * @param onWhichThread
     * @param <R1>
     * @return
     */
    public <R1> TaskChain<T,R1> next(TaskAction<T,R1> action, int onWhichThread){
        TaskChain<T,R1> mFirstTask =new TaskChain<>(action,onWhichThread);
        mFirstTask.setData(mData);
        mFirstTask.mFirst=mFirstTask;
        return mFirstTask;
    }

    /**
     * 开始处理任务链
     * @param activity
     * @param threadPool
     * @param hostThread
     * @param taskChain
     */
    public static void processTaskChain(final Activity activity, final ThreadPoolExecutor threadPool, final Thread hostThread, final TaskChain taskChain){
        if(taskChain==null||activity.isFinishing()||isActivityDestroy(activity)) return;
        if(taskChain.mOnWitchThread==TaskChain.TYPE_THREAD_ON_MAIN){
            if(hostThread==Thread.currentThread()){
                taskChain.mAction.run();
                if(taskChain.mNext!=null) {
                    taskChain.mNext.setData(taskChain.mAction.getReturn());
                    processTaskChain(activity,threadPool,hostThread,taskChain.mNext);
                }
            }
            else
                activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        taskChain.mAction.run();
                        if(taskChain.mNext!=null){
                            taskChain.mNext.setData(taskChain.mAction.getReturn());
                            processTaskChain(activity,threadPool,hostThread,taskChain.mNext);
                        }
                    }
                });
        }
        else{
            if(hostThread==Thread.currentThread()){
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        taskChain.mAction.run();
                        if(taskChain.mNext!=null){
                            taskChain.mNext.setData(taskChain.mAction.getReturn());
                            processTaskChain(activity,threadPool,hostThread,taskChain.mNext);
                        }
                    }
                });
            }
            else{
                taskChain.mAction.run();
                if(taskChain.mNext!=null){
                    taskChain.mNext.setData(taskChain.mAction.getReturn());
                    processTaskChain(activity,threadPool,hostThread,taskChain.mNext);
                }
            }
        }
    }

    private static boolean isActivityDestroy(Activity activity){
        if(Build.VERSION.SDK_INT<17)
            return false;
        return activity.isDestroyed();
    }
}
