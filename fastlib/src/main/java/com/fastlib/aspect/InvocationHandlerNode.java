package com.fastlib.aspect;

import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by sgfb on 2020\01\13.
 * 切面调用形成一条逻辑链,有先后顺序
 */
public abstract class InvocationHandlerNode<T extends Annotation>{
    private static final String TAG=InvocationHandlerNode.class.getSimpleName();

    private Object mHost;
    private Class mProxyClass;
    private Method mProxyMethod;
    protected T mAnnotation;
    protected InvocationHandlerNode mNext;
    protected ResultWrapper mResult;

    protected abstract void handle(Object host,Class proxyClass,Method proxyMethod)throws Exception;

    public InvocationHandlerNode(T mAnnotation) {
        this.mAnnotation = mAnnotation;
    }

    public void start(Object host, ResultWrapper previewResult,Class proxyClass, Method proxyMethod){
        try{
            mHost=host;
            mProxyClass=proxyClass;
            mProxyMethod=proxyMethod;
            mResult=previewResult;
            handle(host,proxyClass,proxyMethod);
        } catch (Exception e) {
            if(host instanceof AspectErrorListener){
                ((AspectErrorListener)host).onAspectError(e);
            }
            else{
                Log.w(TAG,"发生了一个切面异常,但是Controller没有处理切面异常");
                e.printStackTrace();
            }
        }
    }

    protected void startNext(Object result){
        if(mNext!=null) {
            mResult.result=result;
            mNext.start(mHost, mResult, mProxyClass, mProxyMethod);
        }
    }

    public InvocationHandlerNode getNext(){
        return mNext;
    }
}
