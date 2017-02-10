package com.fastlib.app;

import android.app.Activity;
import android.support.v4.util.Pair;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by sgfb on 16/12/29.
 * 任务链
 */
public class TaskChain{
    public static final int TYPE_THREAD_ON_MAIN=1;
    public static final int TYPE_THREAD_ON_WORK=2;

    private TaskChain mNext=null;
    private TaskChain mLast=null;
    private Pair<Integer,Runnable> mTask;

    public TaskChain(Runnable runnable){
        this(TYPE_THREAD_ON_WORK,runnable);
    }

    public TaskChain(int type,Runnable runnable){
        mTask=new Pair<>(type,runnable);
    }

    public TaskChain next(Runnable runnable){
        return next(TYPE_THREAD_ON_WORK,runnable);
    }

    public TaskChain next(int type,Runnable runnable){
        if(mLast==null)
            mLast=new TaskChain(type,runnable);
        else{
            TaskChain tc=new TaskChain(type,runnable);
            mLast.mNext=tc;
            mLast=tc;
        }
        if(mNext==null)
            mNext=mLast;
        return this;
    }

    /**
     * 开始处理任务链
     * @param activity
     * @param threadPool
     * @param hostThread
     * @param taskChain
     */
    public static void processTaskChain(final Activity activity, final ThreadPoolExecutor threadPool, final Thread hostThread, final TaskChain taskChain){
        if(taskChain==null) return;
        if(taskChain.mTask.first==TYPE_THREAD_ON_MAIN){
            if(hostThread==Thread.currentThread()) {
                taskChain.mTask.second.run();
                processTaskChain(activity,threadPool,hostThread,taskChain.mNext);
            }
            else
                activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        taskChain.mTask.second.run();
                        processTaskChain(activity,threadPool,hostThread,taskChain.mNext);
                    }
                });
        }
        else{
            if(hostThread==Thread.currentThread()){
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        taskChain.mTask.second.run();
                        processTaskChain(activity,threadPool,hostThread,taskChain.mNext);
                    }
                });
            }
            else{
                taskChain.mTask.second.run();
                processTaskChain(activity,threadPool,hostThread,taskChain.mNext);
            }
        }
    }
}