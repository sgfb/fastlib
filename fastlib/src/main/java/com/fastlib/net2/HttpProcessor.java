package com.fastlib.net2;

import com.fastlib.db.SaveUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 2019/12/10
 * E-mail:602687446@qq.com
 * 此类对Http进行请求.通过{@link Request}给定的参数经过解释调用{@link SimpleHttpCoreImpl}达到请求和回调
 */
public class HttpProcessor implements Runnable{
    private Request mRequest;
    private byte[] mServerBody;

    public HttpProcessor(Request request) {
        mRequest = request;
    }

    @Override
    public void run(){
        SimpleHttpCoreImpl httpCore=new SimpleHttpCoreImpl(mRequest.getUrl(),mRequest.getMethod());

        //填充头部
        for(Map.Entry<String, List<String>> entry:mRequest.getHeader().entrySet()){
            for(String header:entry.getValue()){
                httpCore.addHeader(entry.getKey(),header);
            }
        }

        boolean needClientBody=MethodDefinition.POST.equals(mRequest.getMethod())||MethodDefinition.PUT.equals(mRequest.getMethod());
        if(needClientBody){
            //填充输出体
            httpCore.addPendingInputStream(loadParams(mRequest.getParams()));
        }

        //开始连接
        try {
            httpCore.begin();
            //TODO 计算是否有返回
//            ResponseHeader serverHeader=httpCore.getResponseHeader();
//            boolean needServerBody=serverHeader.getHeaderFirst("");
            InputStream in=httpCore.getInputStream();
            mServerBody= SaveUtil.loadInputStream(in,false);
            httpCore.end();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            callbackProcess();
        }
    }

    /**
     * 拼接字符串参数
     * @param params    键值列表
     */
    private InputStream loadParams(Map<String,List<String>> params) {
        if (params == null || params.size() <= 0)
            return null;
        StringBuilder sb=new StringBuilder();

        for(Map.Entry<String,List<String>> entry:params.entrySet()){
            for(String value:entry.getValue()){
                if(value!=null) {
                    try {
                        sb.append(entry.getKey()).append('=')
                                .append(URLEncoder.encode(value,"UTF-8"))
                                .append('&');
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return  new ByteArrayInputStream(sb.toString().getBytes());
    }

    /**
     * 回调回请求方
     */
    private void callbackProcess(){
        Listener listener=mRequest.getListener();

        if(listener!=null){
            listener.onResponseSuccess(mRequest,mServerBody);
        }
    }
}
