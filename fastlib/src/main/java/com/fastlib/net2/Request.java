package com.fastlib.net2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 2019/12/10
 * E-mail:602687446@qq.com
 * 单次Http请求所有交互窗口
 */
public class Request {
    private String mUrl;
    private String mMethod;
    private Map<String,List<String>> mHeader = new HashMap<>();
    private Map<String, List<String>> mParams = new HashMap<>();
    private Map<String, List<String>> mFiles = new HashMap<>();
    private Listener mListener;

    public Request(String url) {
        this(url, "GET");
    }

    public Request(String url, String method) {
        mUrl = url;
        mMethod = method;
    }

    /**
     * 添加一个布尔型请求参数,如果存在,覆盖第一个
     */
    public Request put(String key, boolean value) {
        return put(key, Boolean.toString(value));
    }

    /**
     * 添加短整型请求参数,如果存在,覆盖第一个
     */
    public Request put(String key, short value) {
        return put(key, String.valueOf(value));
    }

    /**
     * 添加整型请求参数,如果存在,覆盖第一个
     */
    public Request put(String key, int value) {
        return put(key, Integer.toString(value));
    }

    /**
     * 添加长整型请求参数,如果存在,覆盖第一个
     */
    public Request put(String key, long value) {
        return put(key, Long.toString(value));
    }

    /**
     * 添加单精浮点请求参数,如果存在,覆盖第一个
     */
    public Request put(String key, float value) {
        return put(key, String.valueOf(value));
    }

    /**
     * 添加双精浮点请求参数,如果存在,覆盖第一个
     */
    public Request put(String key, double value) {
        return put(key, String.valueOf(value));
    }

    /**
     * 添加字符串请求参数,如果存在则覆盖
     */
    public Request put(String key, String value) {
        if (mParams == null)
            mParams = new HashMap<>();
        List<String> currValueList=mParams.get(key);
        if(currValueList==null){
            currValueList=new ArrayList<>();
            mParams.put(key,currValueList);
        }
        else currValueList.clear();
        currValueList.add(value);
        return this;
    }

    public void setListener(Listener listener){
        mListener=listener;
    }

    public String getUrl(){
        return mUrl;
    }

    public String getMethod(){
        return mMethod;
    }

    public Map<String,List<String>> getHeader(){
        return mHeader;
    }

    public Map<String,List<String>> getParams(){
        return mParams;
    }

    public Map<String,List<String>> getFiles(){
        return mFiles;
    }

    public Listener getListener(){
        return mListener;
    }
}
