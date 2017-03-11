package com.fastlib.db;

/**
 * Created by sgfb on 17/1/6.
 * 过滤命令
 */
public abstract class FilterCommand{
    public final static int TYPE_AND=1;
    public final static int TYPE_OR=2;
    protected FilterCondition mCondition;
    protected FilterCommand mNext=null;
    protected FilterCommand mLast=null;

    public abstract int getType();

    public FilterCommand(FilterCondition condition){
        mCondition=condition;
    }

    public FilterCommand and(FilterCondition condition){
        FilterCommand fc=new And(condition);
        if(mNext==null){
            mNext=fc;
            mLast=fc;
        }
        else{
            mLast.mNext=fc;
            mLast=fc;
        }
        return this;
    }

    public FilterCommand or(FilterCondition condition){
        FilterCommand fc=new Or(condition);
        if(mNext==null){
            mNext=fc;
            mLast=fc;
        }
        else{
            mLast.mNext=fc;
            mLast=fc;
        }
        return this;
    }

    public FilterCondition getFilterCondition(){
        return mCondition;
    }

    public FilterCommand getNext(){
        return mNext;
    }
}