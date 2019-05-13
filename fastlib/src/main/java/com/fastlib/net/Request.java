package com.fastlib.net;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.fastlib.annotation.NetCallback;
import com.fastlib.app.module.ModuleLife;
import com.fastlib.base.Refreshable;
import com.fastlib.db.FastDatabase;
import com.fastlib.db.ServerCache;
import com.fastlib.net.bean.ResponseStatus;
import com.fastlib.net.exception.DiscardException;
import com.fastlib.net.listener.Listener;
import com.fastlib.net.mock.MockProcess;
import com.fastlib.net.param_parse.ParamParserManager;
import com.fastlib.utils.Reflect;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author sgfb
 * 请求体
 * 每个任务都是不同的，{@link NetManager}会根据属性来配置请求，调整请求开始完成或失败后不同的事件
 */
public class Request{
    public static final int CHUNK_TYPE_AUTO=1;
    public static final int CHUNK_TYPE_OPEN=2;
    public static final int CHUNK_TYPE_CLOSE=3;

    private boolean isCallbackByWorkThread;         //特殊情况下建议网络请求在工作线程回调
    private boolean isCancel;
    private boolean isSuppressWarning;              //压制警告
    private boolean isAcceptGlobalCallback;         //是否接受全局回调监听.默认true
    private boolean isReplaceChinese;               //是否替换中文url,默认为true
    private boolean useFactory;                     //是否使用预设值
    private boolean isSendGzip;                     //指定这次请求发送时是否压缩成gzip流
    private boolean isReceiveGzip;                  //指定这次请求是否使用gzip解码
    private boolean isUseGlobalParamParser;
    private byte[] mByteStream;                     //原始字节流，如果这个值存在就不会发送mParams参数了.如果存在但是长度为0发送mParams参数json化数据
    private int mChunkType =CHUNK_TYPE_AUTO;
    private long mResourceExpire;                   //资源过期时间
    private long mIntervalSendFileTransferEvent=1000;//间隔多久发送上传和下载文件广播
    private String method;
    private String mUrl;
    private String mRootAddress;              //自定义根地址
    private List<Pair<String, String>> mSendCookies;
    private Downloadable mDownloadable;
    private Map<String,List<String>> mReceiveHeader;
    private List<ExtraHeader> mSendHeadExtra;       //额外发送的头部信息
    private List<Pair<String,File>> mFiles;
    private List<Pair<String,String>> mParams;
    private RequestType mType = RequestType.DEFAULT;
    private Object mTag;                            //额外信息
    private Listener mListener;
    private Type[] mGenericType;                    //根据Listener生成的返回类类型存根
    private ServerCache mCacheManager;              //缓存这个请求的数据管理
    private ThreadPoolExecutor mExecutor;           //运行在指定线程池中,如果未指定默认在公共的线程池中
    private MockProcess mMock;                      //模拟数据
    private ResponseStatus mResponseStatus=new ResponseStatus(); //返回包裹信息，尽量不要置null
    private Thread mThread;
    private ParamParserManager mParamParserManager;
    private Refreshable mRefresh;
    private ModuleLife mHostLife;

    public Request() {
        this("");
    }

    public Request(String url) {
        this("POST", url);
    }

    /**
     * 使用模拟数据来初始化请求
     */
    public Request(MockProcess mock){
        this("");
        mMock=mock;
    }

    public Request(String url,String method) {
        this.method = method.toUpperCase();
        mUrl = isReplaceChinese?transferSpaceAndChinese(url):url;
        isAcceptGlobalCallback=true;
        isReplaceChinese=true;
        isSendGzip=false;
        isReceiveGzip=false;
        useFactory = true;
        isUseGlobalParamParser=true;
        mParams = new ArrayList<>();
        mFiles = new ArrayList<>();
        mSendHeadExtra = new ArrayList<>();
        mParamParserManager=new ParamParserManager();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Request))
            return false;
        Request another = (Request) o;
        //如果url和上传的参数，文件都相同那么认为这个网络请求是同一个
        return another == this || (TextUtils.equals(mUrl,another.getUrl()) && another.getParamsRaw().equals(mParams) && another.getFiles().equals(mFiles));
    }

    public void start()  {
        start(false);
    }

    public void start(boolean forceRefresh) {
        if (mCacheManager != null)
            mCacheManager.refresh(forceRefresh);
        else NetManager.getInstance().netRequest(this);
    }

    public ParamParserManager getParamParserManager(){
        return mParamParserManager;
    }

    /**
     * 查找第一个某个键位置
     */
    private int paramsIndexOf(String key){
        if(mParams!=null){
            for(int i=0;i<mParams.size();i++)
                if(TextUtils.equals(mParams.get(i).first,key))
                    return i;
        }
        return -1;
    }

    /**
     * 增加一组数据到某个键中
     */
    public <T> Request addAll(String key,List<T> list){
        if(list!=null&&!list.isEmpty())
            for(T t:list)
                add(key,t);
        return this;
    }

    /**
     * 添加字符串请求参数
     */
    public Request add(String key,String value){
        if(mParams==null)
            mParams=new ArrayList<>();
        mParams.add(Pair.create(key,value));
        return this;
    }

    public Request add(String key,CharSequence charSequence){
        return add(key,charSequence.toString());
    }

    public Request add(String key,boolean value){
        return add(key,Boolean.toString(value));
    }

    /**
     * 添加整数
     */
    public Request add(String key,int value){
        return  add(key,Integer.toString(value));
    }

    /**
     * 添加单精浮点数
     */
    public Request add(String key,float value){
        return add(key,Float.toString(value));
    }

    /**
     * 添加长整形数
     */
    public Request add(String key,long value){
        return add(key,Long.toString(value));
    }

    /**
     * 添加双精浮点数
     */
    public Request add(String key,double value){
        return add(key,Double.toHexString(value));
    }

    /**
     * 添加短整型对象
     */
    public Request add(String key,short value){
        return add(key,Short.toString(value));
    }

    /**
     * 添加上传文件
     */
    public Request add(String key,File file){
        mFiles.add(Pair.create(key,file));
        return this;
    }

    /**
     * 添加Json对象
     */
    public Request add(String key,Object obj){
        if(isUseGlobalParamParser) NetManager.getInstance().getGlobalParamParserManager().parserParam(true,this,key,obj);
        mParamParserManager.parserParam(true,this,key,obj);
        return this;
    }

    /**
     * 添加字符串请求参数,如果存在则覆盖第一个
     */
    public Request put(String key,String value) {
        if (mParams == null)
            mParams = new ArrayList<>();
        int index=paramsIndexOf(key);
        if(index!=-1)
            mParams.remove(index);
        mParams.add(Pair.create(key,value));
        return this;
    }

    public Request put(String key,CharSequence charSequence){
        return put(key,charSequence.toString());
    }

    public Request put(String key,boolean value){
        return put(key,Boolean.toString(value));
    }


    /**
     * 添加短整型请求参数,如果存在,覆盖第一个
     */
    public Request put(String key,short value){
        return put(key,String.valueOf(value));
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
    public Request put(String key,long value){
        return put(key,Long.toString(value));
    }

    /**
     * 添加单精浮点请求参数,如果存在,覆盖第一个
     */
    public Request put(String key,float value){
        return put(key,String.valueOf(value));
    }

    /**
     * 添加双精浮点请求参数,如果存在,覆盖第一个
     */
    public Request put(String key,double value){
        return put(key,String.valueOf(value));
    }

    /**
     * 添加json对象,如果存在,覆盖第一个
     */
    public Request put(String key,Object obj){
        if(isUseGlobalParamParser) NetManager.getInstance().getGlobalParamParserManager().parserParam(false,this,key,obj);
        mParamParserManager.parserParam(false,this,key,obj);
        return this;
    }

    public Request put(Object obj){
        put(null,obj);
        return this;
    }

    /**
     * 简易地添加请求参数
     */
    public Request put(List<Pair<String,String>> params) {
        if (mParams == null)
            mParams = params;
        else
            mParams.addAll(params);
        return this;
    }


    /**
     * 发送文件
     */
    public Request put(String key, File file) {
        if (mFiles == null)
            mFiles = new ArrayList<>();
        mFiles.add(new Pair<>(key,file));
        return this;
    }

    /**
     * 发送文件列表
     */
    public Request putFile(List<Pair<String,File>> fileParams) {
        if (mFiles == null) mFiles = fileParams;
        else mFiles.addAll(fileParams);
        return this;
    }

    /**
     * 参数递增
     */
    public void increment(String key, int count){
        int index;
        if ((index=checkNumberParams(key))==-1)
            return;
        Pair<String,String> pair=mParams.get(index);
        int value=Integer.parseInt(pair.second);
        mParams.remove(pair);
        mParams.add(0,Pair.create(key,Integer.toString(value+count)));
    }

    /**
     * 参数递减
     */
    public void decrease(String key, int count){
        int index;
        if ((index=checkNumberParams(key))==-1)
            return;
        Pair<String,String> pair=mParams.get(index);
        int value=Integer.parseInt(pair.second);
        mParams.remove(pair);
        mParams.add(0,Pair.create(key,Integer.toString(value-count)));
    }

    /**
     * 删除参数
     * @param key 键
     * @param delAll 如果单键对多值是否全部删除.true全部删除 false仅删除第一个
     */
    public void removeParam(String key,boolean delAll){
        List<Pair<String,String>> needRemoveList=new ArrayList<>();

        for(Pair<String,String> param:mParams){
            if(TextUtils.equals(key,param.first)){
                needRemoveList.add(param);
                if(!delAll) break;
            }
        }
        mParams.removeAll(needRemoveList);
    }

    /**
     * 检查是否是数字参数
     */
    private int checkNumberParams(String key){
        int index=-1;
        if (mParams == null) {
            mParams = new ArrayList<>();
            mParams.add(Pair.create(key,"0"));
            return index;
        }
        if ((index=paramsIndexOf(key))==-1) {
            mParams.add(Pair.create(key,"0"));
            return index;
        }
        try {
            Integer.parseInt(mParams.get(index).second);
            return index;
        } catch (NumberFormatException e){
            //转换异常不处理
        }
        return index;
    }

    /**
     * 获取类型索引
     */
    private int getTypeIndex(StringBuilder sb) {
        int index = sb.indexOf(",");
        if (index == -1)
            return -1;
        String strIndex = sb.substring(index + 1);
        try {
            sb.delete(sb.length() - 2, sb.length());
            return Integer.parseInt(strIndex);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public Map<String,String> getParams(){
        Map<String,String> map=new HashMap<>();
        if(mParams==null) return map;
        for(Pair<String,String> pair:mParams)
            map.put(pair.first,pair.second);
        return map;
    }

    public List<Pair<String,String>> getParamsRaw(){
        return mParams;
    }

    /**
     * 发送参数
     */
    public void setParams(List<Pair<String,String>> params) {
        if (params == null&&mParams!=null)
            mParams.clear();
        else
            mParams = params;
    }

    /**
     * 发送文件列表
     * @param files 字符串键文件值对
     */
    public void setFiles(List<Pair<String,File>> files) {
        if (files == null) mFiles.clear();
        else mFiles = files;
    }

    public List<Pair<String,File>> getFiles() {
        return mFiles;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    /**
     * 设置网络回调监听
     * @param l 监听器
     * @return 网络请求本身
     */
    public Request setListener(final Listener l){
        mListener=l;
        if(mListener==null||(mGenericType!=null&&mGenericType.length>0)) //如果指定了实体类型，不自动解析实体类型
            return this;
        mGenericType=new Type[3];
        //泛型解析,如果是Object和byte[]就返回原始字节流,String返回字符,其它类型就尝试使用gson解析
        NetCallback netCallback=Reflect.findAnnotation(l.getClass(),NetCallback.class,true);
        Method[] ms = l.getClass().getDeclaredMethods();

        if(netCallback==null) throw new IllegalStateException("RequestCallbackFun can't be null!");
        //使用遍历免去手动输入方法参数（但同时略微降低性能）
        List<Method> duplicateList=new ArrayList<>();
        for (Method m : ms) {
            String methodFullDescription=m.toString();
            if (netCallback.value().equals(m.getName())&&!methodFullDescription.contains("volatile")){
                duplicateList.add(m);
            }
        }
        Method realMethod = null;
        //所有参数都必须不是Object
        for (Method m : duplicateList){
            boolean allNotObject=true;
            Type[] types=m.getGenericParameterTypes();

            for(Type t:types){
                if(t==Object.class) allNotObject=false;
            }
            if(allNotObject){
                realMethod=m;
                break;
            }
        }
        if(realMethod!=null){
            Type[] types=realMethod.getGenericParameterTypes();

            if(types!=null){
                int typeIndex=0;
                for(int i=0;i<Math.min(3,types.length);i++){
                    Type type=types[i];
                    if(type!=Request.class)
                        mGenericType[typeIndex++]=type;
                }
            }
        }
        return this;
    }

    /**
     * 取消网络请求
     */
    public void cancel(){
        if(isCancel) return;
        isCancel=true;
        if(mThread!=null) mThread.interrupt();
        if(mListener != null)
            mListener.onErrorListener(this,new DiscardException("手动取消网络请求 " + mUrl));
    }

    /**
     * 开启缓存时间,这个方法需要在设置回调后使用
     * @param context   上下文
     * @param cacheName 缓存名（唯一缓存名）
     */
    public Request setCacheTime(Context context, String cacheName, long liveTime) {
        setCacheTime(context, cacheName, liveTime, null);
        return this;
    }

    /**
     * 开启缓存时间,这个方法需要在设置回调后使用,如果这个请求有指定运行线程池则指定缓存器也使用这个线程池
     * @param context    上下文
     * @param cacheName  缓存名（唯一缓存名）
     * @param toDatabase 保持到指定数据库
     */
    public Request setCacheTime(Context context, String cacheName, long liveTime, @Nullable String toDatabase) {
        FastDatabase database = TextUtils.isEmpty(toDatabase) ? FastDatabase.getDefaultInstance(context) : FastDatabase.getInstance(context,toDatabase);
        if (mExecutor == null)
            mCacheManager = new ServerCache(this, cacheName, database);
        else
            mCacheManager = new ServerCache(this, cacheName, database, mExecutor);
        mCacheManager.setCacheTimeLife(liveTime);
        return this;
    }

    /**
     * 空格和汉字替换成unicode
     */
    private String transferSpaceAndChinese(String str){
        if(TextUtils.isEmpty(str))
            return "";
        StringBuilder sb=new StringBuilder(str);

        for(int i=0;i<sb.length();i++){
            char c=sb.charAt(i);
            if(c>='\u4e00'&&c<='\u9fa5'){
                try {
                    sb.deleteCharAt(i);
                    sb.insert(i,URLEncoder.encode(String.valueOf(c),"UTF-8").toCharArray());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString().replace(" ","%20"); //最后空格置换
    }

    public byte[] getByteStream() {
        return mByteStream;
    }

    public Request setByteStream(byte[] byteStream) {
        mByteStream = byteStream;
        return this;
    }

    public Listener getListener() {
        return mListener;
    }

    public String getUrl() {
        String rootAddress= mRootAddress ==null?"": mRootAddress;
        return rootAddress+mUrl;
    }

    public String getMethod() {
        return method;
    }

    public Request setMethod(String method) {
        this.method = method.toUpperCase();
        return this;
    }

    public Request setDownloadable(Downloadable d) {
        mDownloadable = d;
        return this;
    }

    public Downloadable getDownloadable() {
        return mDownloadable;
    }

    public boolean isUseFactory() {
        return useFactory;
    }

    public Request setUseFactory(boolean useFactory) {
        this.useFactory = useFactory;
        return this;
    }

    public Request setCustomRootAddress(String address){
        mRootAddress =address;
        return this;
    }

    public String getCustomRootAddress(){
        return mRootAddress;
    }

    public boolean downloadable() {
        return mDownloadable != null && mDownloadable.getTargetFile() != null && mDownloadable.getTargetFile().exists();
    }

    public void setCurrThread(){
        mThread=Thread.currentThread();
    }

    public Thread getCurrThread(){
        return mThread;
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

    public List<Pair<String, String>> getSendCookies() {
        return mSendCookies;
    }

    public void setSendCookies(List<Pair<String, String>> sendCookies) {
        mSendCookies = sendCookies;
    }

    public void putSendCookies(String key,String value){
        if(mSendCookies==null)
            mSendCookies=new ArrayList<>();
        mSendCookies.add(Pair.create(key,value));
    }

    public void setGenericType(Type[] type) {
        mGenericType = type;
    }

    public Type[] getGenericType() {
        return mGenericType;
    }

    public boolean isReceiveGzip() {
        return isReceiveGzip;
    }

    public void setReceiveGzip(boolean receiveGzip) {
        isReceiveGzip = receiveGzip;
    }

    public boolean isSendGzip() {
        return isSendGzip;
    }

    public void setSendGzip(boolean sendGzip) {
        isSendGzip = sendGzip;
    }

    public Request putHeader(String key, String value){
        if(mSendHeadExtra ==null)
            mSendHeadExtra =new ArrayList<>();
        ExtraHeader extra=new ExtraHeader();
        extra.canDuplication =false;
        extra.field=key;
        extra.value=value;
        mSendHeadExtra.add(extra);
        return this;
    }

    public Request removeHeader(String key){
        if(mSendHeadExtra ==null) return this;
        List<ExtraHeader> needDeleteList=new ArrayList<>();
        for(ExtraHeader head: mSendHeadExtra)
            if(head.field.equals(key))
                needDeleteList.add(head);
        for(ExtraHeader head:needDeleteList)
            mSendHeadExtra.remove(head);
        return this;
    }

    public Request addHeader(String key, String value){
        if(mSendHeadExtra ==null)
            mSendHeadExtra =new ArrayList<>();
        ExtraHeader extra=new ExtraHeader();
        extra.canDuplication =true;
        extra.field=key;
        extra.value=value;
        mSendHeadExtra.add(extra);
        return this;
    }

    public Request setSendHeader(List<ExtraHeader> headers){
        mSendHeadExtra=headers;
        return this;
    }

    public List<ExtraHeader> getSendHeadExtra() {
        return mSendHeadExtra;
    }

    public ServerCache getCacheManager() {
        return mCacheManager;
    }

    public ThreadPoolExecutor getExecutor() {
        return mExecutor;
    }

    public Request setExecutor(ThreadPoolExecutor executor) {
        mExecutor = executor;
        return this;
    }

    public MockProcess getMock() {
        return mMock;
    }

    public Request setMock(MockProcess mock) {
        mMock = mock;
        return this;
    }

    public long getIntervalSendFileTransferEvent() {
        return mIntervalSendFileTransferEvent;
    }

    public Request setIntervalSendFileTransferEvent(long intervalSendFileTransferEvent) {
        if(intervalSendFileTransferEvent<0) intervalSendFileTransferEvent=0;
        mIntervalSendFileTransferEvent = intervalSendFileTransferEvent;
        return this;
    }

    public ResponseStatus getResponseStatus() {
        return mResponseStatus;
    }

    public Request setResponseStatus(ResponseStatus responseStatus){
        if(responseStatus==null) mResponseStatus.clear();
        else mResponseStatus = responseStatus;
        return this;
    }

    public String getReceiveHeader(String key){
        if(mReceiveHeader!=null){
            List<String> list=mReceiveHeader.get(key);
            if(!list.isEmpty()) return list.get(0);
        }
        return null;
    }

    public Map<String, List<String>> getReceiveHeader() {
        return mReceiveHeader;
    }

    public Request setReceiveHeader(Map<String, List<String>> receiveHeader) {
        mReceiveHeader = receiveHeader;
        return this;
    }

    public boolean isReplaceChinese() {
        return isReplaceChinese;
    }

    public Request setReplaceChinese(boolean replaceChinese) {
        isReplaceChinese = replaceChinese;
        return this;
    }

    public boolean isAcceptGlobalCallback() {
        return isAcceptGlobalCallback;
    }

    public Request setAcceptGlobalCallback(boolean acceptGlobalCallback) {
        isAcceptGlobalCallback = acceptGlobalCallback;
        return this;
    }

    public Request setSuppressWarning(boolean suppressWarning){
        isSuppressWarning=suppressWarning;
        return this;
    }

    public boolean getSuppressWarning(){
        return isSuppressWarning;
    }

    public boolean isCancel(){
        return isCancel;
    }

    public void setChunkType(int chunkType){
        mChunkType=chunkType;
    }

    public int getChunkType(){
        return mChunkType;
    }

    @Override
    public String toString(){
        StringBuilder paramsStr=new StringBuilder();
        StringBuilder uploadFileStr=new StringBuilder();

        paramsStr.append("params:").append("[");
        if(mParams!=null&&!mParams.isEmpty()){
            for(Pair<String,String> pair:mParams)
                paramsStr.append("{").append(pair.first).append(",").append(pair.second).append("}").append(",");
            paramsStr.deleteCharAt(paramsStr.length()-1); //去掉最后的逗号
        }
        paramsStr.append("]");
        uploadFileStr.append("files:").append("[");
        if(mFiles!=null&&!mFiles.isEmpty()){
            for(Pair<String,File> pair:mFiles)
                uploadFileStr.append("{").append(pair.first).append(",").append(pair.second).append("}").append(",");
            uploadFileStr.deleteCharAt(uploadFileStr.length()-1);
        }
        uploadFileStr.append("]");
        return getUrl() + " " + method + "\n" +
                paramsStr +" "+ uploadFileStr;
    }

    public long getResourceExpire() {
        return mResourceExpire;
    }

    public void setResourceExpire(long resourceExpire) {
        this.mResourceExpire = resourceExpire;
    }

    public Request reverseCancel(){
        isCancel=false;
        return this;
    }

    public boolean isCallbackByWorkThread() {
        return isCallbackByWorkThread;
    }

    public Request setCallbackByWorkThread(boolean callbackByWorkThread) {
        isCallbackByWorkThread = callbackByWorkThread;
        return this;
    }

    public Request setRefreshable(Refreshable refreshable){
        mRefresh=refreshable;
        return this;
    }

    public ModuleLife getHostLify() {
        return mHostLife;
    }

    public Request setHostLifecycle(ModuleLife mHostLifecycle) {
        this.mHostLife = mHostLifecycle;
        return this;
    }

    /**
     * 是否显示刷新
     * @param status true显示 false不显示
     */
    public void refreshVisibility(boolean status){
        if(mRefresh!=null) mRefresh.setRefreshStatus(status);
    }

    /**
     * 请求类型
     * 1.默认 一切行动照正常规则来
     * 2.全局请求 不受模块限制，独立于模块之外
     * 3.必达请求 在请求开始的时候存入数据库，仅成功送达后删除，并且在错误返回后重试
     */
    public enum RequestType {
        DEFAULT,
        GLOBAL,
        MUSTSEND
    }

    public static class ExtraHeader {
        public boolean canDuplication; //add或者put
        public String field;
        public String value;

        public ExtraHeader(){}

        public ExtraHeader(boolean canDuplication, String field, String value) {
            this.canDuplication = canDuplication;
            this.field = field;
            this.value = value;
        }
    }
}