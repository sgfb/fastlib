package com.fastlib.net;

/**
 * Created by sgfb on 16/2/11.
 */
public interface Listener<T>{

    void onResponseListener(Request r,T result);

    void onErrorListener(Request r,String error);
}