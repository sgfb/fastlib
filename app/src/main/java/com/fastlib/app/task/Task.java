package com.fastlib.app.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 17/9/1.
 * rx风格任务链.现有功能是任务线性化、任务线程切换、平铺和集合任务
 */
public class Task<R>{
    private int mCycleIndex=-1; //默认往左移。如果是循环移到0,如果是跳出式任务为-2
    private Action mAction;
    private Task mPrevious;
    private Task mNext;
    private Task mCycler;
    private R[] mCycleData; //循环参数
    private List mCycleResult=new ArrayList();

    /**
     * 空参数开始生成任务链
     * @return 任务链头部
     */
    public static Task<Object> begin(){
        return begin(new Object());
    }

    /**
     * 实体参数开始生成任务链
     * @param realParam 参数
     * @param <R> 返回类型
     * @return 任务链头部
     */
    public static <R> Task<R> begin(final R realParam){
        return begin(new Action<Object,R>(){
            @Override
            protected R execute(Object param){
                return (R) realParam;
            }
        });
    }

    /**
     * 由事件触发的任务开始头部
     * @param action 行为
     * @param <T> 参数
     * @param <R> 返回
     * @return 任务链头部
     */
    public static <T,R> Task<R> begin(Action<T,R> action){
        return begin(action, ThreadType.WORK);
    }

    /**
     * 有事件触发的任务开始头部，指定运行线程
     * @param action 行为
     * @param whichThread 运行在指定线程上
     * @param <T> 参数
     * @param <R> 返回
     * @return 任务链头部
     */
    public static <T,R> Task<R> begin(Action<T,R> action, ThreadType whichThread){
        Task<R> task=new Task();
        task.mAction=action;
        task.mAction.setThreadType(whichThread);
        return task;
    }

    /**
     * 开始一个循环任务（平铺参数）
     * @param param 被平铺参数
     * @param <T> 平铺参数类型
     * @return 任务链头部
     */
    public static <T> Task<T> beginCycle(final T[] param){
        Task task=new Task();
        task.mCycleIndex=0;
        task.mAction=new Action<T,T[]>(){

            @Override
            protected T[] execute(T p) {
                return param;
            }
        };
        return task;
    }

    /**
     * 开始一个循环任务
     * @param param 被平铺的参数
     * @param <T> 平铺参数类型
     * @return 任务链头部
     */
    public static <T> Task<T> beginCycle(final List<T> param){
        return (Task<T>) beginCycle(param.toArray());
    }

    /**
     * 结束之前的循环任务。聚拢循环结果开始下一个任务
     * @param action 聚拢之前循环结果事件
     * @param <T> 计算的事件结果
     * @return 跳出之前循环任务的事件
     */
    public <T> Task<T> again(Action<List<R>,T> action){
        return again(action,ThreadType.WORK);
    }

    /**
     * 结束之前的循环任务。聚拢循环结果开始下一个任务
     * @param action 聚拢之前循环结果事件
     * @param whichThread 指定线程
     * @param <T> 计算的事件结果
     * @return 跳出之前循环任务的事件
     */
    public <T> Task<T> again(Action<List<R>,T> action, ThreadType whichThread){
        mNext=new Task();
        mNext.mAction=action;
        mNext.mAction.setThreadType(whichThread);
        mNext.mPrevious=this;
        mNext.mCycleIndex=-2;
        return mNext;
    }

    /**
     * 接上下一个任务
     * @param action 下一个任务行为
     * @param <T> 参数
     * @return 下一个任务
     */
    public <T> Task<T> next(Action<R,T> action){
        return next(action,ThreadType.WORK);
    }

    /**
     * 接上下一个任务
     * @param action 下一个任务行为
     * @param whichThread 指定线程
     * @param <T> 参数
     * @return 下一个任务
     */
    public <T> Task<T> next(Action<R,T> action, ThreadType whichThread){
        mNext=new Task();
        mNext.mAction=action;
        mNext.mAction.setThreadType(whichThread);
        mNext.mPrevious=this;
        //如果自身不是循环任务，但是有在循环任务之中。或者自身是循环任务，下一个任务在循环中。自身循环优先级高于非自身
        if(mCycler!=null)
            mNext.mCycler=mCycler;
        if(mCycleIndex==0)
            mNext.mCycler=this;
        return mNext;
    }

    /**
     * 循环任务
     * @param action 循环任务的行为
     * @return 下一个任务（循环任务）
     */
    public Task<R> cycle(Action<R,R[]> action){
        return cycle(action,ThreadType.WORK);
    }

    /**
     * 循环任务
     * @param action 循环任务行为
     * @param whichThread 运行在指定线程上
     * @return 下一个任务（循环任务）
     */
    public Task<R> cycle(Action<R,R[]> action, ThreadType whichThread){
        mNext=new Task();
        mNext.mAction=action;
        mNext.mPrevious=this;
        mNext.mCycleIndex=0;
        return mNext;
    }

    /**
     * 循环任务
     * @param params 需要循环的参数
     * @return 下一个任务（循环任务）
     */
    public Task<R> cycle(final R[] params){
        return cycle(params,ThreadType.WORK);
    }

    public Task<R> cycle(final R[] params,ThreadType whichThread){
        return cycle(new Action<R, R[]>() {
            @Override
            public R[] execute(R param) {
                return params;
            }
        },whichThread);
    }

    /**
     * 行为执行完毕后的返回
     * @return 指定返回
     */
    public Object getReturn(){
        Object result=null;
        if(mCycleIndex<0){ //默认和跳出类型
            if(mNext!=null)
                mNext.mCycleResult.add(mAction.getReturn());
            return mAction.getReturn();
        }
        if(mCycleIndex==0&&mCycleData==null) { //循环类型，并且未生成循环数据
            mCycleData = (R[]) mAction.getReturn();
            if(mCycleData!=null&&mCycleData.length>0) return mCycleData[mCycleIndex++];
            else return null;
        }
        //还是循环类型，但是已有循环数据
        if(mCycleIndex>0) {
            if((mCycleData==null||mCycleIndex>=mCycleData.length)) //循环终结
            return null;
            else result=mCycleData[mCycleIndex++]; ////返回循环中指定索引
        }
        if(mNext!=null&&mNext.mCycleIndex==-2) //如果下一个任务是循环跳出类型
            mNext.mCycleResult.add(result);
        return result;
    }

    public void setParam(Object obj){
        if(mCycleIndex==-2) //如果是跳出式任务，给予循环存储数据
            mAction.setParam(mCycleResult);
        else
            mAction.setParam(obj);
    }

    /**
     * 执行行为.循环任务只执行一次拿到数组数据
     */
    public void process(){
        if(mCycleIndex<=0) mAction.process();
    }

    /**
     * 获取下一个任务（可能在循环中）
     * @return 下一个执行的任务
     */
    public Task getNext(){
        Task task=this;
        if(task.mCycler!=null&&(task.mCycler.mCycleData==null||task.mCycler.mCycleData.length==task.mCycler.mCycleIndex)) //如果链接到一个循环器，并且循环器索引已到尾端，结束这个循环器
            return task.mNext;
        if(task.mNext==null||task.mNext.mCycleIndex==-2) //如果下一个任务存在并且是跳出循环类型
            return task.mCycler!=null?task.mCycler:task.mNext;
        return task.mNext;
    }

    public boolean isStopNow(){
        return mAction.isInterrupt();
    }

    public Task getPrevious() {
        return mPrevious;
    }

    public ThreadType getOnWhichThread() {
        return mAction.getThreadType();
    }
}