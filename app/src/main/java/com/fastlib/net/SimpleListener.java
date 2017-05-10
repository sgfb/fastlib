package com.fastlib.net;

/**
 * Created by sgfb on 17/4/26.
 * 最少可以只重载一个onResponseListener就能使用的网络回调监听
 */
public abstract class SimpleListener<T> implements Listener<T>{

    @Override
    public void onRawData(Request r,byte[] data) {
        //被适配
    }

    @Override
    public void onTranslateJson(Request r,String json) {
        //被适配
    }

    @Override
    public void onErrorListener(Request r, String error){
        //被适配
    }
}