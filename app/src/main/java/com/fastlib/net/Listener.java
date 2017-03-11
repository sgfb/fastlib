package com.fastlib.net;

/**
 * Created by sgfb on 16/12/28.
 */

public interface Listener<T>{

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