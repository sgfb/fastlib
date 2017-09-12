package com.fastlib.app.task;

import android.app.Activity;
import android.os.Build;
import android.os.Looper;
import android.support.v4.app.Fragment;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by sgfb on 17/9/6.
 * 启动线性Task必要的启动器
 */
public class TaskLauncher{
    private Object mHost;
    private ThreadPoolExecutor mThreadPool;
    private volatile boolean mStopFlag;

    public TaskLauncher(Activity activity, ThreadPoolExecutor threadPool) {
        mHost = activity;
        mThreadPool = threadPool;
    }

    public TaskLauncher(Fragment fragment,ThreadPoolExecutor threadPool){
        mHost=fragment;
        mThreadPool=threadPool;
    }

    /**
     * 开始线性任务
     * @param task
     */
    public void startTask(Task task){
        Task firstTask=task;
        while(firstTask.getPrevious()!=null)
            firstTask=firstTask.getPrevious();
        threadDispatch(firstTask);
    }

    /**
     * 线性任务线程调度
     * @param task
     */
    private void threadDispatch(final Task task){
        if(task.getOnWhichThread()== ThreadType.MAIN){
            if(Looper.myLooper()==Looper.getMainLooper())
                processTask(task);
            else getHostActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    processTask(task);
                }
            });
        }
        else{
            if(Looper.myLooper()==Looper.getMainLooper())
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        processTask(task);
                    }
                });
            else
                processTask(task);
        }
    }

    /**
     * 线性任务处理具体任务
     * @param task
     */
    private void processTask(Task task){
        task.process(); //执行事件后才有返回

        if(checkStopStatus(task)) return; //中断任务事件
        Object obj=task.getReturn();
        Task nextTask=task.getNext();
        if(nextTask!=null){
            nextTask.setParam(obj);
            threadDispatch(nextTask);
        }
    }

    public Activity getHostActivity(){
        if(mHost instanceof Activity) return (Activity) mHost;
        else return ((Fragment)mHost).getActivity();
    }

    private boolean checkStopStatus(Task task){
        return hostIsFinish()||task.isStopNow()||mStopFlag;
    }

    private boolean hostIsFinish(){
        if(mHost instanceof Activity){
            Activity activity= (Activity) mHost;
            return activity.isFinishing()||(Build.VERSION.SDK_INT>=17&&activity.isDestroyed());
        }
        else {
            Fragment fragment= (Fragment) mHost;
            return fragment.isRemoving()||fragment.isDetached();
        }
    }

    public void stopNow(boolean stopFlag){
        mStopFlag = stopFlag;
    }
}