package com.fastlib.app.task;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.fastlib.net.NetManager;
import com.fastlib.net.Request;
import com.fastlib.net.exception.NetException;
import com.fastlib.net.listener.GlobalListener;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Created by sgfb on 17/9/5.
 * 网络事件.因为网络请求不能在主线程中，所以execute中的行为必须置与子线程中。而executeAdapt置与指定的线程中
 */
public abstract class NetAction<P,R> extends Action<Request,R>{
    private static final Object mLock=new Object();
    private Object mResponseObj;
    private Object mCookedData;
    private R mReturnValue;

    /**
     * 是否支持全局回调处理后的“熟”数据
     * @return true支持，false不支持
     */
    protected boolean isCookResultData(){
        return false;
    }

    protected abstract R executeAdapt(P r,Request request);

    @Override
    protected R execute(final Request param){
        GlobalListener globalListener=NetManager.getInstance().getGlobalListener();
        Method[] methods=getClass().getDeclaredMethods();
        Type rType=Object.class;
        try {
            byte[] response = NetManager.getInstance().netRequestPromptlyBack(param);
            for(Method m:methods){
                if("executeAdapt".equals(m.getName())){
                    Type[] paramsType=m.getGenericParameterTypes();
                    for(Type paramType:paramsType)
                        if(paramType!=Object.class&&paramType!=Request.class){
                            rType=paramType;
                            break;
                        }
                }
                if(rType!=Object.class) break;
            }
            if(response!=null){
                String responseStr=new String(response);
                if(globalListener!=null){
                    response=globalListener.onRawData(param,response,0,0);
                    responseStr=globalListener.onTranslateJson(param,responseStr);
                }
                if(rType==Object.class||rType==byte[].class||rType==byte.class)
                    mResponseObj = response;
                else if(rType==String.class)
                    mResponseObj =responseStr;
                else {
                    Gson gson=new Gson();
                    try{
                        mResponseObj =gson.fromJson(responseStr,rType);
                        mCookedData=globalListener.onResponseListener(param,mResponseObj,null);
                    }catch (JsonParseException e){
                        globalListener.onErrorListener(param,new NetException("请求:" + param + "\n解析时出现异常:" + e.getMessage() + "\njson字符串:" + responseStr));
                        stopTask(); //如果异常了，停止任务链
                    }
                }
            }
            checkNetBackStatus(param);

            if(mThreadType==ThreadType.MAIN){
                Handler handler=new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1);
                            mReturnValue=executeAdapt(isCookResultData()? (P) mCookedData :(P) mResponseObj,param);
                            synchronized (mLock){
                                mLock.notifyAll();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                synchronized (mLock){
                    try {
                        mLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return mReturnValue;
            }
            else
                return executeAdapt(isCookResultData()? (P) mCookedData :(P) mResponseObj,param);
        } catch (Exception e) {
            e.printStackTrace();
            stopTask();
            globalListener.onErrorListener(param,e);
            return null;
        }
    }

    private void checkNetBackStatus(Request request){
        if(request.getResponseStatus().code<200||request.getResponseStatus().code>299){
            stopTask();
            Log.d(NetAction.class.getSimpleName(),"net action error:"+request.getResponseStatus().message);
        }
    }

    /**
     * 强制返回运行在子线程中
     * @return 线程类型（固定子线程）
     */
    @Override
    public ThreadType getThreadType() {
        return ThreadType.WORK;
    }
}