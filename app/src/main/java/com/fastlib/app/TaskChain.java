package com.fastlib.app;

/**
 * Created by sgfb on 17/2/22.
 * 任务队列基本单元
 */
public class TaskChain<T,R1>{
    public static final int TYPE_THREAD_ON_MAIN=1;
    public static final int TYPE_THREAD_ON_WORK=2;

    TaskAction<T,R1> mAction;
    TaskChain mNext;
    TaskChain mPreviousCycleTask; //前置循环任务
    int mOnWitchThread;
    int mCycleCount;
    int mCycleRound; //循环堆叠
    R1[] mCycleObj;

    public TaskChain(){
        mCycleCount=0;
        mCycleRound =-1;
    }

    public TaskChain(TaskAction<T,R1> action){
        this(action,TYPE_THREAD_ON_WORK);
    }

    public TaskChain(TaskAction<T,R1> action, int type){
        mOnWitchThread=type;
        mAction=action;
    }

    public <R2> TaskChain<R1,R2> next(TaskAction<R1,R2> action){
        return next(action,TYPE_THREAD_ON_WORK);
    }

    public <R2> TaskChain<R1,R2> next(TaskAction<R1,R2> action,int type){
        mNext=new TaskChain<>(action,type);
        return mNext;
    }

//    public TaskChain<R1> cycle(int cycleCount,R1[] array,TaskSimpleAction<R1> action){
//        mCycleCount=cycleCount;
//        mCycleRound =0;
//        mNext=new TaskChain<>(action);
//        mCycleObj=array;
//        return mNext;
//    }

    public TaskChain<T,R1> again(T t,TaskAction<T,R1> action){
        TaskChain<T,R1> task=new TaskChain<>();
        task.setData(t);
        return task;
    }


    public void setData(T data){
        mAction.setData(data);
    }
}