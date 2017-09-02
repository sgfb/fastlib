package com.fastlib.app;

/**
 * Created by sgfb on 17/8/20.
 * 简单任务事件单位
 */
public abstract class TaskSimpleAction<R> implements Runnable{
    private R mResult;

    public abstract R call();

    @Override
    public void run(){
        mResult=call();
    }

    public R getResult(){
        return mResult;
    }
}
