package com.fastlib.net2;

/**
 * Created by sgfb on 2019/12/10
 * E-mail:602687446@qq.com
 * 网络请求回调
 */
public interface Listener{

    void onResponseSuccess(Request request,byte[] data);
}
