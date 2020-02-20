package com.fastlib.utils;

import com.fastlib.net2.Listener;
import com.fastlib.net2.Request;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by sgfb on 2020\01\10.
 * 网络请求代理器
 * 默认运行在当前线程中（当前线程不能是主线程）方法中参数名为网络请求键,参数值为请求值
 * 地址 {@link RequestTo}
 * 参数注解 {@link MapValue} {@link Name}
 * 如果参数中有{@link com.fastlib.net2.Request}切不为空当做自定义Request处理
 * 如果参数中有{@link com.fastlib.net2.Listener}则此请求可以运行在主线程中,而返回值必定为空
 */
public class NetRequestAgent{

    private NetRequestAgent(){}

    public static <T> T genAgent(Class<T> cla){
        //检查必要参数
        for(Method method:cla.getMethods()){
            RequestTo requestTo=method.getAnnotation(RequestTo.class);
            if(requestTo==null) throw new IllegalStateException("请求接口中未定义RequestTo");
        }

        return (T) Proxy.newProxyInstance(cla.getClassLoader(), new Class[]{cla}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
                Request request=null;
                Listener listener=null;
                if(args!=null){
                    for(Object arg:args){
                        if(arg instanceof Request)
                            request= (Request) arg;
                        else if(arg instanceof Listener)
                            listener= (Listener) arg;
                    }
                }

                if(request==null){
                    RequestTo requestTo=method.getAnnotation(RequestTo.class);
                    request=new Request(requestTo.url(),requestTo.method());
                }

                if(args!=null){
                    //填充参数到请求中
                    Annotation[][] annotations=method.getParameterAnnotations();
                    for(int i=0;i<args.length;i++){
                        Annotation paramAnno=annotations[i]!=null&&annotations[i].length>0?annotations[i][0]:null;

                        if(paramAnno instanceof Name){
                            Name name= (Name) paramAnno;
                            request.put(name.value(),args[i]);
                        }
                        else if(paramAnno instanceof MapValue){
                            request.put(args[i]);
                        }
                    }
                }

                if(listener==null)
                    return request.startSyc(method.getReturnType());
                else {
                    request.setListener(listener);
                    request.start();
                    return null;
                }
            }
        });
    }
}
