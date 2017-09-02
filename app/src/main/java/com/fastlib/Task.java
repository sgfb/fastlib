package com.fastlib;

/**
 * Created by sgfb on 17/9/1.
 */
public class Task<R>{
    private DefaultAction mAction;
    private Task mPrevious;
    private Task mNext;
    private Task mCycler;
    private R[] mCycleData;
    private int mCycleIndex=-1; //默认往左移。如果循环移到0

    public static <T,R> Task<R> begin(DefaultAction<T,R> action){
        Task<R> task=new Task();
        task.mAction=action;
        return task;
    }

    public <T> Task<R> next(DefaultAction<T,R> action){
        mNext=new Task();
        mNext.mAction=action;
        mNext.mPrevious=this;
        //如果自身不是循环任务，但是有在循环任务之中。或者自身是循环任务，下一个任务在循环中。自身循环优先级高于非自身
        if(mCycler!=null)
            mNext.mCycler=mCycler;
        if(mCycleIndex==0)
            mNext.mCycler=this;
        return mNext;
    }

    public Task<R> cycle(DefaultAction<R[],R> action){
        mNext=new Task();
        mNext.mAction=action;
        mNext.mPrevious=this;
        mNext.mCycleIndex=0;
        return mNext;
    }

    public Object getReturn(){
        if(mCycleIndex==-1) return mAction.getReturn();
        if(mCycleIndex==0)
            mCycleData= (R[]) mAction.getReturn();
        if(mCycleData==null||mCycleIndex>=mCycleData.length) return null;
        return mCycleData[mCycleIndex++];
    }

    /**
     * 获取下一个任务（可能在循环中）
     * @return 下一个执行的任务
     */
    public Task getNext(){
        if(mCycler!=null)
            if(mCycler.mCycleData==null||mCycler.mCycleData.length<=mCycler.mCycleIndex) return null;
        return mCycler!=null?mCycler:mNext;
    }

    public DefaultAction getAction() {
        return mAction;
    }

    public Task getPrevious() {
        return mPrevious;
    }

    public Task getCycler() {
        return mCycler;
    }

    public int getCycleIndex() {
        return mCycleIndex;
    }

    public R[] getCycleData() {
        return mCycleData;
    }
}