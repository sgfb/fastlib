package com.fastlib.net;

/**
 * Created by sgfb on 16/2/11.
 */
public interface Listener {

    void onResponseListener(Request r,String result);

    void onErrorListener(Request r,String error);
}
