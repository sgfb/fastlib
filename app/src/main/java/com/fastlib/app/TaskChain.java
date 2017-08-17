package com.fastlib.app;

import java.util.List;

/**
 * Created by sgfb on 17/2/22.
 * 任务队列基本单元
 */
public class TaskChain<T,R1>{
    public static final int TYPE_THREAD_ON_MAIN=1;
    public static final int TYPE_THREAD_ON_WORK=2;

    TaskAction<T,R1> mAction;
    TaskChain mNext;
    TaskChain mFirst;
    TaskCycle mCycle;
    int mOnWitchThread;

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
        mNext.mFirst=mFirst;
        return mNext;
    }

    public TaskCycle<R1> cycleTask(List<R1> list){
        return mCycle=new TaskCycle(this,list);
    }

    public void setData(T data){
        mAction.setData(data);
    }

    public TaskChain getFirst() {
        return mFirst;
    }
}