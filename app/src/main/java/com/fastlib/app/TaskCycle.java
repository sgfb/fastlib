package com.fastlib.app;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sgfb on 17/8/14.
 */
public class TaskCycle<T>{
    List<T> mList;
    T[] mArray;
    TaskChain mTaskChain;

    public TaskCycle(TaskChain tc,List<T> list){
        mTaskChain=tc;
        mList = list;
    }

    public TaskCycle(TaskChain tc,T[] array){
        mTaskChain=tc;
        mArray=array;
    }

    public List<T> getList(){
        if(mList!=null) return mList;
        else return Arrays.asList(mArray);
    }
}