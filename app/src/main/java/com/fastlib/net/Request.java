package com.fastlib.net;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求体<br/>
 * 每个任务都是不同的，（NetQueue）会根据属性来配置请求，调整请求开始完成或失败后不同的事件
 */
public class Request implements Comparable<Request>{
    private boolean sFile;
    private boolean hadRootAddress; //是否已加入根地址
    private boolean useFactory; //是否使用预设值
    private String method;
    private String mUrl;
    private String mGenericName;
    private Downloadable mDownloadable;
    private Map<String,String> mParams;
    private Map<String,File> mFiles;
    private RequestType mType=RequestType.DEFAULT;
    private Object mTag; //额外信息
    private String[] mSession; //留存的session
    private NetProcessor mProcessor;
    //加入activity或者fragment可以提升安全性
    private Activity mActivity;
    private Fragment mFragment;
    private Listener mListener;

    public Request(String url){
        this("POST",url);
    }

    public Request(){
        this("");
    }

    public Request(String method,String url){
        this.method=method.toUpperCase();
        mUrl=url;
        useFactory=true;
    }

    @Override
    public int compareTo(@NonNull Request another){
        return another.getUrl().equals(mUrl)?1:0;
    }

    public Request start(){
        NetQueue.getInstance().netRequest(this);
        return this;
    }

    public Request start(Fragment fragment){
        mFragment=fragment;
        return start();
    }

    public Request start(Activity activity){
        mActivity=activity;
        return start();
    }

    /**
     * 简易地添加请求参数
     * @param key
     * @param value
     */
    public Request put(String key,String value){
        if(mParams==null)
            mParams=new HashMap<>();
        mParams.put(key, value);
        return this;
    }

    public Request put(String key,int value){
        return put(key,Integer.toString(value));
    }

    /**
     * 简易地添加请求参数
     * @param params
     * @return
     */
    public Request put(Map<String,String> params){
        if(mParams==null)
            mParams=params;
        else
            mParams.putAll(params);
        return this;
    }


    public Request put(String key,File file){
        if(mFiles==null)
            mFiles=new HashMap<>();
        mFiles.put(key,file);
        return this;
    }

    public Request putFile(Map<String,File> fileParams){
        if(mFiles==null)
            mFiles=fileParams;
        else
            mFiles.putAll(fileParams);
        return this;
    }

    /**
     * 参数递增
     * @param key
     * @param count
     */
    public void increment(String key,int count){
        if(!checkNumberParams(key))
            return;
        String value=mParams.get(key);
        mParams.put(key, Integer.toString(Integer.parseInt(value) + count));

    }

    /**
     * 参数递减
     * @param key
     * @param count
     */
    public void decrease(String key,int count){
        if(!checkNumberParams(key))
            return;
        String value=mParams.get(key);
        mParams.put(key, Integer.toString(Integer.parseInt(value)-count));
    }

    private boolean checkNumberParams(String key){
        if(mParams==null){
            mParams=new HashMap<>();
            mParams.put(key,"0");
            return false;
        }
        if(!mParams.containsKey(key)) {
            mParams.put(key,"0");
            return false;
        }
        try{
            Integer.parseInt(mParams.get(key));
            return true;
        }catch (NumberFormatException e){
            mParams.put(key,"0");
            return false;
        }
    }

    public  Map<String,String> getParams(){
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

    public void setUrl(String mUrl){
        this.mUrl = mUrl;
    }

    public Request setListener(Listener l){
        mListener=l;
        return this;
    }

    public Listener getListener(){
        return mListener;
    }

    public String getUrl(){return mUrl;}

    public String getMethod(){
        return method;
    }

    public Request setMethod(String method){
        this.method=method.toUpperCase();
        return this;
    }

    public boolean isFile() {
        return sFile;
    }

    public void setFile(boolean sFile) {
        this.sFile = sFile;
    }

    public Request setDownloadable(Downloadable d){
        mDownloadable=d;
        return this;
    }

    public Downloadable getDownloadable(){
        return mDownloadable;
    }

    public boolean isUseFactory() {
        return useFactory;
    }

    public Request setUseFactory(boolean useFactory) {
        this.useFactory = useFactory;
        return this;
    }

    public boolean isHadRootAddress() {
        return hadRootAddress;
    }

    public void setHadRootAddress(boolean hadRootAddress) {
        this.hadRootAddress = hadRootAddress;
    }

    public boolean downloadable(){
        return mDownloadable!=null&&mDownloadable.getTargetFile()!=null&&mDownloadable.getTargetFile().exists();
    }

    public Request setHost(Context context){
        if(context instanceof Activity)
            mActivity= (Activity) context;
        return this;
    }

    public Request setHost(Activity activity){
        mActivity=activity;
        return this;
    }

    public Request setHost(Fragment fragment){
        mFragment=fragment;
        return this;
    }

    /**
     * 仅在开始进行网络请求时使用这个方法
     * @param processor
     */
    public void startProcess(NetProcessor processor){
        mProcessor=processor;
    }

    /**
     * 取消网络请求
     * @return
     */
    public boolean cancel(){
        return mProcessor!=null&&mProcessor.stop();
    }

    public String[] getSession(){
        return mSession;
    }

    public void setSession(String[] session) {
        mSession = session;
    }

    public Object getTag() {
        return mTag;
    }

    public void setTag(Object tag) {
        mTag = tag;
    }

    public RequestType getType() {
        return mType;
    }

    public void setType(RequestType type) {
        mType = type;
    }

    public String getGenericName() {
        return mGenericName;
    }

    public void setGenericName(String genericName) {
        mGenericName = genericName;
    }

    public Object getHost(){
        if(mFragment!=null)
            return mFragment;
        if(mActivity!=null)
            return mActivity;
        return null;
    }

    @Override
    public String toString(){
        return "url:"+mUrl+" method:"+method+" params:"+mParams+" uploadFile:"+mFiles;
    }

    /**
     * 请求类型</br>
     * 1.默认 一切行动照正常规则来</br>
     * 2.全局请求 不受模块限制，独立于模块之外</br>
     * 3.必达请求 在请求开始的时候存入数据库，仅成功送达后删除，并且在错误返回后重试
     */
    public enum RequestType{
        DEFAULT,
        GLOBAL,
        MUSTSEND
    }
}