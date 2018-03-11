package com.fastlib.utils;

import android.text.TextUtils;

import com.fastlib.net.Request;
import com.fastlib.net.SimpleListener;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

/**
 * Created by sgfb on 18/3/9.
 * 测试工具类
 */
public class TestUtil{
    public static final Object sLock=new Object();

    /**
     * 接口测试
     * 开始请求-->本地或服务器网络是否正常-y->返回的数据解析异常(当前默认json解析)-y->业务逻辑判断-y->接口单元测试通过
     *                 |                           |                                 |
     *                 n                           n                                 n
     *                 |                           |                                 |
     *                 V                           V                                 V
     *              物理层异常                  数据模型不符合                     业务逻辑具体异常
     * @param request 网络请求
     * @param typeToken 数据模型
     * @param checker 业务逻辑判断器
     * @param <T> 期望的数据模型
     * @return 如果为空代表通过否则为异常信息
     * @throws Exception
     */
    public static <T> String netInterfaceCheck(Request request, final TypeToken<T> typeToken, final SessionCheck<T> checker)throws Exception{
        final StringBuilder sb=new StringBuilder();
        sb.append("接口:").append(request).append("\n");
        request.setCallbackByWorkThread(true);
        request.setListener(new SimpleListener<String>(){

            @Override
            public void onResponseListener(Request r,String result) {
                Gson gson=new Gson();
                try{
                    T entity=gson.fromJson(result,typeToken.getType());
                    String testResult=checker.check(entity);
                    if(TextUtils.isEmpty(testResult)) sb.setLength(0);
                    else sb.append(testResult);
                }catch (JsonParseException e){
                    sb.append("数据模型不符合");
                }finally {
                    synchronized (sLock){
                        sLock.notifyAll();
                    }
                }
            }

            @Override
            public void onErrorListener(Request r, String error) {
                super.onErrorListener(r, error);
                sb.append("物理层异常");
                synchronized (sLock){
                    sLock.notifyAll();
                }
            }
        });
        request.start();
        synchronized (sLock){
            sLock.wait();
        }
        return sb.toString();
    }
}