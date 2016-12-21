package com.fastlib.net;

/**
 * Created by sgfb on 16/2/11.
 */
public interface Listener<T>{

    /**
     * 数据返回时调用,这个方法运行在子线程中,可以进行一些耗时操作
     */
    void onDataResult(String json);

    /**
     * 数据原型回调
     * @param r
     * @param result
     */
    void onResponseListener(Request r,T result);

    /**
     * 错误回调
     * @param r
     * @param error
     */
    void onErrorListener(Request r,String error);
}