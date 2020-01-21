package com.fastlib.aspect;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import leo.android.cglib.proxy.MethodInterceptor;
import leo.android.cglib.proxy.MethodProxy;

/**
 * Created by sgfb on 2020\01\07.
 * MVC框架实现方法调用切面逻辑.控制器部分
 */
public class ControllerInvocationHandler implements MethodInterceptor{
    public Map<Class,InvocationHandlerNode> mAspectAnnotation=new HashMap<>();
    /**
     * 过程注解控制 不调用本体方法也不返回和阻断流程
     */
    private Map<Class,MethodInterceptor> mProcessInterceptorMap =new HashMap<>();
    /**
     * 代理注解控制 控制整个流程的功能
     */
    private Map<Class,MethodInterceptor> mProxyInterceptorMap=new HashMap<>();

    @Override
    public Object intercept(final Object o, final Object[] objects, final MethodProxy methodProxy) throws Exception{
        Annotation[] annotations=methodProxy.getOriginalMethod().getAnnotations();

        if(annotations!=null){
            List<InvocationHandlerNode> actionList=new ArrayList<>();
            for(Annotation a:annotations){
                InvocationHandlerNode nextNode=mAspectAnnotation.get(a.getClass());
                if(nextNode!=null){
                    actionList.add(nextNode);
                }
            }

            if(!actionList.isEmpty()){
                ResultWrapper wrapper=new ResultWrapper();
                for(InvocationHandlerNode node:actionList)
                    node.start(o,wrapper,methodProxy.getClass(),methodProxy.getOriginalMethod());
                return wrapper.result;
            }
        }
//        if(annotations!=null){
//            MethodInterceptor proxyInterceptor=null;
//            for(Annotation annotation:annotations){
//                MethodInterceptor processInterceptor= mProcessInterceptorMap.get(annotation.getClass());
//                if(processInterceptor!=null)
//                    processInterceptor.intercept(o,objects,methodProxy);
//                if(proxyInterceptor==null)
//                    proxyInterceptor=mProxyInterceptorMap.get(annotation.getClass());
//            }
//            if(proxyInterceptor!=null)
//                return proxyInterceptor;
//        }
        return null;
    }
}
