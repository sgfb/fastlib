package com.fastlib.net;

/**
 * Created by sgfb on 16/2/11.
 */
public interface Listener {

    void onResponseListener(String result);

    void onErrorListener(String error);
}
