package com.fastlib.app;

/**
 * Created by sgfb on 17/2/23.
 * 任务队列中最小单位
 */
public abstract class TaskAction<P,R1> implements Runnable{
    private P mData;
    private R1 mReturn;

    public abstract R1 call(P t);

    public TaskAction(){}

    @Override
    public void run(){
        mReturn=call(mData);
    }

    public P getData() {
        return mData;
    }

    public void setData(P data) {
        mData = data;
    }

    public R1 getReturn() {
        return mReturn;
    }
}