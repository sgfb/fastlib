package com.fastlib.utils.bind_view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.fastlib.aspect.AspectSupport;
import com.fastlib.BuildConfig;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.annotation.LocalData;
import com.fastlib.utils.Reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sgfb on 17/1/12.
 * 视图注入到字段和方法中.指定对象类或自动查找（自动查找注解有ContentView类和当前类）
 */
public class ViewInject {
    private static final String TAG=ViewInject.class.getSimpleName();
    private static final Map<String,Class<? extends OnBindViewReceiver>> mBindViewHolderMap=new HashMap<>();

    public static void putBindViewReceiver(String type,Class<? extends OnBindViewReceiver> cla){
        mBindViewHolderMap.put(type,cla);
    }

    public static void removeBindViewReceiver(String type){
        mBindViewHolderMap.remove(type);
    }

    private ViewInject(){}

    public static void inject(Object host,@NonNull View root){
        inject(host,root,null);
    }

    public static void inject(Object host, @NonNull View root,@Nullable Class bindCla) {
        injectViewEvent(host, root,bindCla);
    }

    /**
     * 绑定控件到方法和属性中
     */
    private static void injectViewEvent(Object host,View root,@Nullable Class bindCla) {
        //绑定到自身或指定类
        Class cla = bindCla!=null?bindCla:host.getClass();
        injectMethod(host,root,cla.getDeclaredMethods());
        injectField(host,root,cla.getDeclaredFields());

        //如果自身没有ContentView注解并且不是手动指定的就向上查找有ContentView注解的类并且进行绑定
        if (cla.getAnnotation(ContentView.class) == null&&bindCla==null){
            Class contentViewClass = Reflect.checkParentClassHadAnnotation(cla, ContentView.class);
            if(contentViewClass!=null) injectMethod(host,root,contentViewClass.getDeclaredMethods());
            if(contentViewClass!=null) injectField(host,root,contentViewClass.getDeclaredFields());
        }
    }

    private static void injectMethod(Object host,View root,Method[] methods) {
        if (methods != null && methods.length > 0) {
            for (Method m : methods) {
                try{
                    m.setAccessible(true);
                    Bind vi = m.getAnnotation(Bind.class);
                    LocalData ld = m.getAnnotation(LocalData.class);
                    Deprecated deprecated = m.getAnnotation(Deprecated.class);
                    if (vi != null && ld == null && deprecated == null) {
                        int[] ids = vi.value();

                        if (ids.length > 0) {
                            for (int id : ids) {
                                View v = root.findViewById(id);
                                if (v != null)
                                    bindListener(host,m, v, vi);
                            }
                        }
                    }
                }catch (NoClassDefFoundError e){
                    e.printStackTrace();
                }
            }
        }
    }

    private static void injectField(Object host,View root,Field[] fields) {
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                try{
                    Bind vi = field.getAnnotation(Bind.class);
                    Deprecated deprecated = field.getAnnotation(Deprecated.class);
                    if (vi != null && deprecated == null) {
                        int[] ids = vi.value();
                        if (ids.length > 0) {
                            try {
                                View view = root.findViewById(ids[0]);
                                field.setAccessible(true);
                                field.set(host, view);
                            } catch (IllegalAccessException e) {
                                if (BuildConfig.isShowLog)
                                    System.out.println(e.getMessage());
                            }
                        }
                    }
                }catch (NoClassDefFoundError e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 绑定方法到事件监听中
     */
    private static void bindListener(final Object host, final Method m,View v, final Bind vi) {
        switch (vi.type()) {
            case Bind.TYPE_CLICK:
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        adapterParamInvoke(host,m,v);
                    }
                });
                break;
            case Bind.TYPE_LONG_CLICK:
                v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Object result=adapterParamInvoke(host,m,v);
                        if(result==null) result=false;
                        return (boolean) result;
                    }
                });
                break;
            default:
                Class<? extends OnBindViewReceiver> cla=mBindViewHolderMap.get(vi.type());
                if(cla!=null){
                    try {
                        OnBindViewReceiver receiver=cla.newInstance();
                        receiver.setOnBindViewCallback(new OnBindViewCallback() {
                            @Override
                            public Object toggle(Object... args){
                                return adapterParamInvoke(host,m,args);
                            }
                        });
                        receiver.bindView(v);
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                else Log.w(TAG,"事件绑定时未找到对应type view:"+v+" type:"+vi.type());
                break;
        }
    }

    /**
     * 兼容空参和默认参调用
     * @param m     调用方法
     * @param args  默认参数
     */
    private static Object adapterParamInvoke(Object host,Method m,Object... args){
        if(m.getParameterTypes().length==0)
            return AspectSupport.callMethod(host,m);
        else return AspectSupport.callMethod(host,m,args);
    }
}