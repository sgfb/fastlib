package com.fastlib.net;

/**
 * Created by sgfb on 16/12/28.
 * 网络回调监听
 */
public interface Listener<T>{

    /**
     * 原始字节数据回调
     * @param data 源字节
     */
    void onRawData(Request r,byte[] data);

    /**
     * 数据解析成字符串时回调,这个方法运行在子线程中,可以进行一些耗时操作(在Request中可以命令返回原始字节,那么这个方法将不会被回调)
     * @param json 仅仅只是new String(data)
     */
    void onTranslateJson(Request r,String json);

    /**
     * 数据原型回调
     * @param r 网络请求
     * @param result 返回的实体
     */
    void onResponseListener(Request r,T result);

    /**
     * 错误回调
     * @param r 网络请求
     * @param error 简单的错误信息
     */
    void onErrorListener(Request r,String error);
}