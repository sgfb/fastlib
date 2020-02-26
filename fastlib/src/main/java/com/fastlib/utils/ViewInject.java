package com.fastlib.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;

import com.fastlib.aspect.AspectSupport;
import com.fastlib.BuildConfig;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.annotation.LocalData;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by sgfb on 17/1/12.
 * 视图注入到字段和方法中.指定对象类或自动查找（自动查找注解有ContentView类和当前类）
 */
public class ViewInject {

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
                        String[] idNames = vi.idNames();

                        if (ids.length > 0) {
                            for (int id : ids) {
                                View v = root.findViewById(id);
                                if (v != null)
                                    bindListener(host,m, v, vi);
                            }
                        }
                        if (idNames.length > 0) {
                            for (String idName : idNames) {
                                int id = root.getContext().getResources().getIdentifier(idName, "id", root.getContext().getPackageName());
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
                        String[] idNames = vi.idNames();
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
                        if (idNames.length > 0) {
                            try {
                                int id = root.getContext().getResources().getIdentifier(idNames[0], "id", root.getContext().getPackageName());
                                View view = root.findViewById(id);
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
    private static void bindListener(final Object host, final Method m, View v, final Bind vi) {
        switch (vi.bindType()) {
            case CLICK:
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        adapterParamInvoke(host,m,v);
                    }
                });
                break;
            case LONG_CLICK:
                v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        adapterParamInvoke(host,m,v);
                        return true;
                    }
                });
                break;
            case ITEM_CLICK:
                ((AdapterView) v).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        adapterParamInvoke(host,m,view,position,id);
                    }
                });
                break;
            case ITEM_LONG_CLICK:
                ((AdapterView) v).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        adapterParamInvoke(host,m,parent,view,position,id);
                        return true;
                    }
                });
                break;
            default:
                break;
        }
    }

    /**
     * 兼容空参和默认参调用
     * @param m     调用方法
     * @param args  默认参数
     */
    private static void adapterParamInvoke(Object host,Method m,Object... args){
        if(m.getParameterTypes().length==0)
            AspectSupport.callMethod(host,m);
        else AspectSupport.callMethod(host,m,args);
    }
}