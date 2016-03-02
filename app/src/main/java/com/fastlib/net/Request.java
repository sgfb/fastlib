package com.fastlib.net;

import java.io.File;
import java.util.Map;

/**
 * 请求体
 * 每个任务都是不同的，（NetQueue）会根据属性来配置请求，调整请求开始完成或失败后不同的事件
 */
public class Request implements Comparable{
    private int type;
    private boolean sFile;
    private String method;
    private String mUrl;
    private Downloadable mDownloadable;
    private Listener mListener;
    private Map<String,String> mParams;
    private Map<String,File> mFiles;

    public Request(){
        this("POST","");
    }

    public Request(String method,String mUrl){
        this(method, mUrl,NetQueue.TYPE_INDEPEND);
    }

    public Request(String method,String mUrl,int type){
        this.method=method.toUpperCase();
        this.mUrl = mUrl;
        this.type=type;
    }

    @Override
    public int compareTo(Object another) {
        return 0;
    }

    public  Map<String,String> getParame(){
        return mParams;
    }

    public void setParams(Map<String,String> params){
        mParams=params;
    }

    public void setFiles(Map<String,File> files){
        mFiles=files;
    }

    public Map<String,File> getFiles(){
        return mFiles;
    }

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

    public String getMethod(){
        return method;
    }

    public void setMethod(String method){
        this.method=method.toUpperCase();
    }

    public boolean isFile() {
        return sFile;
    }

    public void setFile(boolean sFile) {
        this.sFile = sFile;
    }

    public void setDownloadable(Downloadable d){
        mDownloadable=d;
    }

    public Downloadable getDownloadable(){
        return mDownloadable;
    }
}