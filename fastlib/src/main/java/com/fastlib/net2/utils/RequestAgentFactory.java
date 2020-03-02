package com.fastlib.net2.utils;

import com.fastlib.aspect.NetResultTransformer;
import com.fastlib.aspect.ResultTransformer;
import com.fastlib.net2.Request;
import com.fastlib.net2.listener.Listener;
import com.google.gson.Gson;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sgfb on 2020\01\10.
 * 网络请求代理生成器
 * 默认运行在当前线程中（当前线程不能是主线程）方法中参数名为网络请求键,参数值为请求值
 * 地址 {@link RequestTo}
 * 参数 {@link MapValue} {@link Name}
 * 如果参数中有{@link com.fastlib.net2.Request}且不为空当做自定义Request处理
 * 如果参数中有{@link Listener}则此请求可以运行在主线程中,而返回值必定为空
 */
public class RequestAgentFactory {
    private final static Map<Class,Object> sCacheAgentMap=new HashMap<>();

    private RequestAgentFactory(){}

    @SuppressWarnings("unchecked")
    public static <T> T genAgent(final Class<T> cla){
        T agent= (T) sCacheAgentMap.get(cla);
        if(agent!=null) return agent;

        T proxy=(T) Proxy.newProxyInstance(cla.getClassLoader(), new Class[]{cla}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
                if(method.getName().equals("toString"))
                    return getClass().getName()+"@"+Integer.toHexString(hashCode());

                RequestTo requestTo=method.getAnnotation(RequestTo.class);
                if(requestTo==null)
                    return null;

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

                if(request==null)
                    request=new Request(requestTo.url(),requestTo.method());

                //静态参数
                FinalParam finalParam=method.getAnnotation(FinalParam.class);
                if(finalParam!=null){
                    String[] keyValuePairs=finalParam.value();
                    for(int i=0;i<keyValuePairs.length/2;i++)
                        request.put(keyValuePairs[i*2],keyValuePairs[i*2+1]);
                }

                //动态参数
                if(args!=null){
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

                if(listener==null) {
                    ResultTransformer resultTransformer=method.getAnnotation(ResultTransformer.class);
                    if(resultTransformer==null) resultTransformer=cla.getAnnotation(ResultTransformer.class);

                    if(resultTransformer!=null){
                        Class<? extends NetResultTransformer> transformerCla=resultTransformer.value();
                        Type type=null;
                        for(Type inter:transformerCla.getGenericInterfaces()){
                            if(inter instanceof ParameterizedType){
                                ParameterizedType pt= (ParameterizedType) inter;
                                if(pt.getRawType()==NetResultTransformer.class)
                                    type=pt.getActualTypeArguments()[0];
                            }
                        }
                        if(type==null||type==Object.class||type==Void.class||type==void.class)
                            request.startSyc();
                        else{
                            Object result=request.startSyc(type);
                            Object transformedResult=transformerCla.newInstance().transform(result);
                            Gson gson=new Gson();
                            String json=gson.toJson(transformedResult);
                            return gson.fromJson(json,method.getReturnType());
                        }
                    }
                    return request.startSyc(method.getReturnType());
                }
                else {
                    request.setListener(listener);
                    request.start();
                    return null;
                }
            }
        });
        sCacheAgentMap.put(cla,proxy);
        return proxy;
    }
}
