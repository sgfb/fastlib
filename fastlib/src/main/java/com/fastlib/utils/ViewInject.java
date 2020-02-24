package com.fastlib.utils;

import android.support.annotation.NonNull;
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
 * 视图注入到字段和方法中.仅对当前和注解有ContentView类有效
 */
public class ViewInject {
    private Object mHost;
    private View mRoot;

    private ViewInject(Object host, @NonNull View root) {
        mHost = host;
        mRoot = root;
        injectViewEvent();
    }

    public static void inject(Object host, @NonNull View root) {
        new ViewInject(host, root);
    }

    /**
     * 绑定控件到方法和属性中
     */
    private void injectViewEvent() {
        Class cla = mHost.getClass();
        Class contentViewClass=null;

        if (cla.getAnnotation(ContentView.class) == null)
            contentViewClass = Reflect.checkParentClassHadAnnotation(cla, ContentView.class);
        //绑定控件方法.自身和有ContentView注解的父类
        injectMethod(cla.getDeclaredMethods());
        if(contentViewClass!=null) injectMethod(contentViewClass.getDeclaredMethods());

        //绑定视图到属性.自身和有ContentView注解的父类
        injectField(cla.getDeclaredFields());
        if(contentViewClass!=null) injectField(contentViewClass.getDeclaredFields());
    }

    private void injectMethod(Method[] methods) {
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
                                View v = mRoot.findViewById(id);
                                if (v != null)
                                    bindListener(m, v, vi);
                            }
                        }
                        if (idNames.length > 0) {
                            for (String idName : idNames) {
                                int id = mRoot.getContext().getResources().getIdentifier(idName, "id", mRoot.getContext().getPackageName());
                                View v = mRoot.findViewById(id);
                                if (v != null)
                                    bindListener(m, v, vi);
                            }
                        }
                    }
                }catch (NoClassDefFoundError e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void injectField(Field[] fields) {
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
                                View view = mRoot.findViewById(ids[0]);
                                field.setAccessible(true);
                                field.set(mHost, view);
                            } catch (IllegalAccessException e) {
                                if (BuildConfig.isShowLog)
                                    System.out.println(e.getMessage());
                            }
                        }
                        if (idNames.length > 0) {
                            try {
                                int id = mRoot.getContext().getResources().getIdentifier(idNames[0], "id", mRoot.getContext().getPackageName());
                                View view = mRoot.findViewById(id);
                                field.setAccessible(true);
                                field.set(mHost, view);
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
    private void bindListener(final Method m, View v, final Bind vi) {
        switch (vi.bindType()) {
            case CLICK:
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        adapterParamInvoke(m,v);
                    }
                });
                break;
            case LONG_CLICK:
                v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        adapterParamInvoke(m,v);
                        return true;
                    }
                });
                break;
            case ITEM_CLICK:
                ((AdapterView) v).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        adapterParamInvoke(m,view,position,id);
                    }
                });
                break;
            case ITEM_LONG_CLICK:
                ((AdapterView) v).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        adapterParamInvoke(m,parent,view,position,id);
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
    private void adapterParamInvoke(Method m,Object... args){
        if(m.getParameterTypes().length==0)
            AspectSupport.callMethod(mHost,m);
        else AspectSupport.callMethod(mHost,m,args);
    }
}