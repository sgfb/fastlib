package com.fastlib.net2;

import java.io.InputStream;

/**
 * Created by sgfb on 2020\01\03.
 * 简单适配类.不需要实现{@link Listener#onRawCallback(Request, InputStream)}和{@link Listener#onError(Request, Exception)}
 */
public abstract class SimpleListener<T> implements Listener<T>{

    @Override
    public void onRawCallback(Request request, InputStream outputStream) {
        //被适配
    }

    @Override
    public void onError(Request request, Exception error) {
        //被适配
    }
}
