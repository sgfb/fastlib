package com.fastlib.net;

import java.util.Map;

/**
 * 网络请求体
 * 每个任务都是不同的，（NetQueue）会根据属性来配置请求，调整请求开始完成或失败后不同的事件
 */
public abstract class Request implements Comparable {
    public String method;
    public String mUrl;
    public int type;
    public Listener mListener;

    public Request(){
        this("POST","");
    }

    public Request(String method,String mUrl){
        this(method, mUrl,NetQueue.TYPE_INDEPEND);
    }

    public Request(String method,String mUrl,int type){
        this.method=method;
        this.mUrl = mUrl;
        this.type=type;
    }

    @Override
    public int compareTo(Object another) {
        return 0;
    }

    public abstract Map<String,String> getParame();

    public int getType(){
        return type;
    }

    public void setUrl(String mUrl){
        this.mUrl = mUrl;
    }

    public void setListener(Listener l){
        mListener=l;
    }

    public Listener getListener(){
        return mListener;
    }

    public String getUrl(){return mUrl;}
}
