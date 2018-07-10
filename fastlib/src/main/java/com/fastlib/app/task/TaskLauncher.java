package com.fastlib.app.task;

import android.app.Activity;
import android.os.Build;
import android.support.v4.app.Fragment;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by sgfb on 17/9/6.
 * 启动线性Task必要的启动器
 */
public class TaskLauncher{
    private volatile boolean mStopFlag;
    private boolean mForceMainThreadFlag;  //强制在主线程中运行
    private ThreadPoolExecutor mThreadPool;
    private NoReturnAction<Throwable> mExceptionHandler; //一个全局的异常处理器,如果Task有对应异常处理器则不调用此处理器
    private EmptyAction mCompleteAction;
    private Object mHost;

    private TaskLauncher(){}

    /**
     * 线性任务线程调度
     * @param task
     */
    private void threadDispatch(final Task task){
        if(mForceMainThreadFlag){
            try {
                processTask(task);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        else{
            mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        if(task.getDelay()>0)
                            Thread.sleep(task.getDelay());
                        if(task.getOnWhichThread()==ThreadType.MAIN)
                            getHostActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    processTask(task);
                                }
                            });
                        else processTask(task);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    /**
     * 线性任务处理具体任务
     * @param task
     */
    private void processTask(Task task){
        try{
            task.process(); //执行事件后才有返回
            if(checkStopStatus(task)){ //中断任务事件
                if(mCompleteAction!=null) mCompleteAction.executeAdapt();
                return;
            }
            Object obj=task.getReturn();
            Task nextTask=task.getNext();
            //过滤任务处理
            if(task.isFilterTask()){
                if(obj!=null&&(obj instanceof Boolean)){
                    Boolean b= (Boolean) obj;
                    if(!b){
                        if(task.isCycleEnd()){
                            while(nextTask!=null&&!nextTask.isAgainTask())
                                nextTask=nextTask.getNext();
                        }
                        else
                            nextTask=task.getCycler();
                    }
                }
                obj=task.getParam(); //过滤任务的参数就是返回，递交给下一个任务
            }
            if(nextTask!=null){
                nextTask.setParam(obj);
                threadDispatch(nextTask);
            }
            else{
                if(mCompleteAction!=null)
                    mCompleteAction.execute(null);
            }
            if(task.isInfiniteTask()) reset(task).startTask(task);
        }catch (Throwable throwable){
            //优先处理任务中存在的异常处理器，如果没有再尝试运行全局异常处理器
            boolean handled=false;
            if(task!=null){
                NoReturnAction<Throwable> taskExceptionHandler=task.getTaskExceptionHandler();
                if(taskExceptionHandler!=null){
                    taskExceptionHandler.execute(throwable);
                    handled=true;
                }
            }
            if(mExceptionHandler!=null&&!handled) mExceptionHandler.execute(throwable);
            if(mCompleteAction!=null) mCompleteAction.execute(null);
        }
    }

    /**
     * 宿主是否已结束生命周期
     * @return true已结束（不继续任务事件）
     */
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

    private boolean checkStopStatus(Task task){
        return hostIsFinish()||task.isStopNow()||mStopFlag;
    }

    /**
     * 开始线性任务
     * @param task
     */
    public void startTask(Task task){
        Task firstTask=task;
        firstTask.clean();
        while(firstTask.getPrevious()!=null)
            firstTask=firstTask.getPrevious();
        threadDispatch(firstTask);
    }

    public Activity getHostActivity(){
        if(mHost instanceof Activity) return (Activity) mHost;
        else return ((Fragment)mHost).getActivity();
    }

    public void stopNow(boolean stopFlag){
        mStopFlag = stopFlag;
    }

    public TaskLauncher reset(Task task){
        if(task==null) return null;
        mStopFlag=true;

        TaskLauncher launcher=new TaskLauncher();
        launcher.mHost=mHost;
        launcher.mThreadPool=mThreadPool;
        launcher.mExceptionHandler=mExceptionHandler;
        launcher.mCompleteAction=mCompleteAction;
        return launcher;
    }

    public static class Builder{
        private TaskLauncher launcher;

        public Builder(Activity activity,ThreadPoolExecutor threadPool){
            init(activity,threadPool);
        }

        public Builder(Fragment fragment,ThreadPoolExecutor threadPool){
            init(fragment,threadPool);
        }

        private void init(Object host,ThreadPoolExecutor threadPoolExecutor){
            launcher=new TaskLauncher();
            launcher.mHost=host;
            launcher.mThreadPool=threadPoolExecutor;
        }

        /**
         * 这次线性任务全局异常处理
         * @param handler 异常处理器
         * @return 线性任务启动器
         */
        public Builder setExceptionHandler(NoReturnAction<Throwable> handler){
            launcher.mExceptionHandler=handler;
            return this;
        }

        /**
         * 无论是正常流程结束还是异常结束，最后都调用这个事件，如果存在的话
         * @param action 结尾任务
         * @return 线性任务启动器
         */
        public Builder setLastTask(EmptyAction action){
            launcher.mCompleteAction=action;
            return this;
        }

        /**
         * 强制运行在主线程中，抛弃线程调度
         * @param forceOnMainThread true运行在主线程中，false无限制
         * @return 线性任务启动器
         */
        public Builder setForceOnMainThread(boolean forceOnMainThread){
            launcher.mForceMainThreadFlag=forceOnMainThread;
            return this;
        }

        public TaskLauncher build(){
            return launcher;
        }
    }
}