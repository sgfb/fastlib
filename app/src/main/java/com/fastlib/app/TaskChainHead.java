package com.fastlib.app;

import android.app.Activity;
import android.os.Build;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by sgfb on 17/2/23.
 * 任务队列头
 */
public class TaskChainHead<T>{
    private T mData;

    private TaskChainHead(){

    }

    private TaskChainHead(T data){
        mData=data;
    }

    /**
     * 给予初始化参数，引导任务链
     * @param data 初始化参数
     * @param <T> 初始参数类型
     * @return 任务链头
     */
    public static <T> TaskChainHead<T> begin(T data){
        return new TaskChainHead<T>(data);
    }

    /**
     * 给予无参数任务，引导任务链
     * @param action 任务
     * @param <R1> 处理的线程类型
     * @return 任务链
     */
    public static <R1> TaskChain<Object, R1> begin(TaskAction<Object,R1> action){
        return new TaskChainHead<>().next(action);
    }

    /**
     * 给予无参数任务，引导任务链，有线程选择
     * @param action 任务
     * @param onWhichThread 处理的线程类型
     * @param <R1> 参数类型
     * @return 任务链
     */
    public static <R1> TaskChain<Object,R1> begin(TaskAction<Object,R1> action,int onWhichThread){
        return new TaskChainHead<>().next(action,onWhichThread);
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

//    public <R1> TaskChain<T,R1> cycle(TaskAction<T[],T> action){
//
//    }

    /**
     * 开始处理任务链
     * @param activity
     * @param threadPool
     * @param hostThread
     * @param taskChain
     */
    public static void processTaskChain(final Activity activity, final ThreadPoolExecutor threadPool, final Thread hostThread, final TaskChain taskChain){
        if(taskChain==null||isActivityDestroy(activity)) return;
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
        return (Build.VERSION.SDK_INT >= 17 && activity.isDestroyed())||activity.isFinishing();
    }
}