package com.fastlib;

/**
 * Created by sgfb on 17/9/1.
 */
public abstract class DefaultAction<P,R> implements Runnable{
    private P mParam;
    private R mReturn;

    public abstract R execute(P param);

    @Override
    public void run(){
        mReturn=execute(mParam);
    }

    public R getReturn(){
        mReturn=execute(mParam);
        return mReturn;
    }

    public void setParam(P param) {
        mParam = param;
    }
}