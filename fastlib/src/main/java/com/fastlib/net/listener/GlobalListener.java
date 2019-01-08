package com.fastlib.net.listener;

import com.fastlib.net.Request;

/**
 * Created by sgfb on 17/7/31.
 * 与{@link Listener}相似.但是全局仅存一个且在每个请求的Listener之前触发
 */
public class GlobalListener{

    /**
     * 请求发送前回调
     * @param request 网络请求
     */
    public void onLaunchRequestBefore(Request request){

    }

    /**
     * 请求开始连接
     * @param request 网络请求
     */
    public void onRequestLaunched(Request request){

    }

    /**
     * 原始字节数据回调
     * data 源字节
     * @return 处理后的源字节
     */
    public byte[] onRawData(Request r,byte[] data,long downloadLength,long uploadLength){
        return data;
    }

    /**
     * 数据解析成字符串时回调,这个方法运行在子线程中,可以进行一些耗时操作(在Request中可以命令返回原始字节,那么这个方法将不会被回调)
     * @param json 仅仅只是new String(data)
     * @return 被处理后的json字符串
     */
    public String onTranslateJson(Request r,String json){
        return json;
    }

    /**
     * 数据原型回调,会进行实体转换猜想，最多3种
     * @param r 网络请求
     * @param result 返回的实体
     * @param result2 可能的返回实体2
     * @return 默认返回非空result
     */
    public Object onResponseListener(Request r,Object result,Object result2){
        if(result!=null) return result;
        else return result2;
    }

    /**
     * 错误回调
     * @param r 网络请求
     * @param error 简单的错误信息
     * @return 被处理后的简单错误信息
     */
    public String onErrorListener(Request r,String error){
        return error;
    }
}