package com.fastlib.app.task;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.fastlib.net.NetManager;
import com.fastlib.net.Request;
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
    private R mReturnValue;

    protected abstract R executeAdapt(P r,Request request);

    @Override
    protected R execute(final Request param) throws IOException {
        Method[] methods=getClass().getDeclaredMethods();
        Type rType=Object.class;
        byte[] response=NetManager.getInstance().netRequestPromptlyBack(param);

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
                mResponseObj = response;
            else if(rType==String.class)
                mResponseObj =new String(response);
            else {
                Gson gson=new Gson();
                try{
                    mResponseObj =gson.fromJson(new String(response),rType);
                }catch (JsonParseException e){
                    e.printStackTrace();
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
                        mReturnValue=executeAdapt((P) mResponseObj,param);
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
            return executeAdapt((P) mResponseObj,param);
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