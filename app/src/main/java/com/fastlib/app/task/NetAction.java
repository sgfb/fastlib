package com.fastlib.app.task;

import android.util.Log;

import com.fastlib.net.NetManager;
import com.fastlib.net.Request;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by sgfb on 17/9/5.
 * 网络事件.因为网络请求不能在主线程中，所以execute中的行为必须置与子线程中。而executeAdapt置与指定的线程中
 */
public abstract class NetAction<P,R> extends Action<Request,R>{
    private static final Object mLock=new Object();

    protected abstract R executeAdapt(P r,Request request);

    @Override
    protected R execute(final Request param) throws IOException {
        Object responseObj=null;
        Method[] methods=getClass().getDeclaredMethods();
        Type rType=Object.class;
        byte[] response;

        response=NetManager.getInstance().netRequestPromptlyBack(param);
        synchronized (mLock){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
            if(rType==Object.class||rType==byte[].class||rType==byte.class)
                responseObj=response;
            else if(rType==String.class)
                responseObj=new String(response);
            else {
                Gson gson=new Gson();
                try{
                    responseObj=gson.fromJson(new String(response),rType);
                }catch (JsonParseException e){
                    e.printStackTrace();
                    stopTask(); //如果异常了，停止任务链
                }
            }
        }
        checkNetBackStatus(param);
        return executeAdapt((P) responseObj,param);
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