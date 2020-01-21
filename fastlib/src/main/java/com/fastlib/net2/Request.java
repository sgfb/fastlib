package com.fastlib.net2;

import com.fastlib.annotation.NetCallback;
import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.net2.param.RequestParam;
import com.fastlib.utils.Reflect;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
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
    private Type mCustomType;       //一个自定义回调类型，优先使用这个参数其次才是解析mListener中方法参数

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

    public Object startSyc(Type type){
        mCustomType=type;
        setCallbackOnWorkThread(true);
        HttpProcessor hp=new HttpProcessor(this);
        hp.run();
        return hp.getResultData();
    }

    public void cancel(){
        //TODO
    }

    public Type getResultType(){
        if(mCustomType!=null)
            return mCustomType;
        else if(mListener!=null)
            return resolveResultType();
        return null;
    }

    /**
     * 解析回调指定类型.如果是Object或byte[]就返回原始字节流,String返回字符,File则联合{@link Request#mDownloadable}来做处理,其它类型就尝试使用gson解析
     * @return  需要回调的类型
     */
    private Type resolveResultType(){
        NetCallback netCallback=Reflect.findAnnotation(mListener.getClass(),NetCallback.class,true);
        if(netCallback==null) throw new IllegalStateException("NetCallback annotation can't be null!");

        Method[] ms = mListener.getClass().getDeclaredMethods();
        for (Method m : ms) {
            String methodFullDescription=m.toString();
            if (netCallback.value().equals(m.getName())&&!methodFullDescription.contains("volatile")){
                //所有参数都必须不是Object,否则当无类型使用
                Type[] paramsType=m.getGenericParameterTypes();
                for(Type type:paramsType){
                    if(type!=Request.class&&type!=Object.class) return type;
                }
            }
        }
        return null;
    }
}
