package com.fastlib.net2;

import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.net2.param.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 2019/12/10
 * E-mail:602687446@qq.com
 * Http请求所有交互窗口
 */
public class Request {
    private boolean isSkipRootAddress;
    private boolean isSkipGlobalListener;
    private boolean isCallbackOnWorkThread;
    private String mUrl;
    private String mMethod;
    private Map<String,List<String>> mHeader = new HashMap<>();
    private RequestParam mParam=new RequestParam();
    private Downloadable mDownloadable;
    private Listener mListener;
    private Statistical mStatistical;

    public Request(String url) {
        this(url, "GET");
    }

    public Request(String url, String method) {
        mUrl = url;
        mMethod = method;
    }

    public Request put(Object value){
        mParam.put(value);
        return this;
    }

    public Request put(String key,Object value){
        mParam.put(key,value);
        return this;
    }

    public Request add(Object value){
        mParam.add(value);
        return this;
    }

    public Request add(String key,Object value){
        mParam.add(key,value);
        return this;
    }

    public Request setListener(Listener listener){
        mListener=listener;
        return this;
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

    public Listener getListener(){
        return mListener;
    }

    public RequestParam getRequestParam(){
        return mParam;
    }

    public Request setDownloadable(Downloadable downloadable){
        mDownloadable=downloadable;
        return this;
    }

    public Downloadable getDownloadable(){
        return mDownloadable;
    }

    public Request setSkipRootAddress(boolean skipRootAddress){
        isSkipRootAddress=skipRootAddress;
        return this;
    }

    public boolean getSkipRootAddress(){
        return isSkipRootAddress;
    }

    public Request setSkipGlobalListener(boolean skipGlobalListener){
        isSkipGlobalListener=skipGlobalListener;
        return this;
    }

    public boolean getSkipGlobalListener(){
        return isSkipGlobalListener;
    }

    public Request setStatistical(Statistical statistical){
        mStatistical=statistical;
        return this;
    }

    public Statistical getStatistical(){
        return mStatistical;
    }

    public Request setCallbackOnWorkThread(boolean callbackOnWorkThread){
        isCallbackOnWorkThread=callbackOnWorkThread;
        return this;
    }

    public boolean getCallbackOnWorkThread(){
        return isCallbackOnWorkThread;
    }

    public void start(){
        ThreadPoolManager.sSlowPool.execute(new HttpProcessor(this));
    }

    public void cancel(){
        //TODO
    }
}
