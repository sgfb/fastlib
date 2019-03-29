package com.fastlib.utils;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;

import com.fastlib.BuildConfig;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.annotation.LocalData;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by sgfb on 17/1/12.
 * 视图注入到字段和方法中.仅对当前和注解有ContentView类有效
 */
public class ViewInject {
    private ThreadPoolExecutor mThreadPool;
    private Object mHost;
    private View mRoot;

    private ViewInject(Object host, @NonNull View root, ThreadPoolExecutor threadPool) {
        mThreadPool = threadPool;
        mHost = host;
        mRoot = root;
        injectViewEvent();
    }

    public static void inject(Object host, @NonNull View root) {
        inject(host, root, null);
    }

    public static void inject(Object host, @NonNull View root, ThreadPoolExecutor threadPool) {
        new ViewInject(host, root, threadPool);
    }

    /**
     * 绑定视图事件到方法上，运行一段代码如果有异常自行处理
     *
     * @param runOnWorkThread
     * @param m
     * @param objs
     */
    private boolean invokeWithoutError(boolean runOnWorkThread, final Method m, final Object... objs) {
        if (runOnWorkThread && mThreadPool != null)
            mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        m.invoke(mHost);  //先尝试绑定无参方法
                    } catch (InvocationTargetException e) { //这个异常是非方法参数异常所以直接显示或抛出
                        if (BuildConfig.isShowLog) {
                            System.out.println("toggle exception");
                            e.printStackTrace();
                        }
                    } catch (IllegalAccessException e) {
                        if (BuildConfig.isShowLog) e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        try {
                            m.invoke(mHost, objs);
                        } catch (IllegalAccessException e1) {
                            if (BuildConfig.isShowLog) {
                                System.out.println("toggle exception");
                                e1.printStackTrace();
                            }
                        } catch (InvocationTargetException e2) {
                            if (BuildConfig.isShowLog) {
                                System.out.println("toggle exception");
                                e2.printStackTrace();
                            }
                        }
                    }
                }
            });
        else
            try {
                Object result = m.invoke(mHost);  //先尝试绑定无参方法
                if (result instanceof Boolean)
                    return (boolean) result;
            } catch (IllegalAccessException | IllegalArgumentException e) {
                try {
                    Object result = m.invoke(mHost, objs);
                    if (result instanceof Boolean)
                        return (Boolean) result;
                } catch (IllegalAccessException | IllegalArgumentException e1) {
                    if (BuildConfig.isShowLog) {
                        System.out.println("toggle exception");
                        e1.printStackTrace();
                    }
                } catch (InvocationTargetException e2) {
                    if (BuildConfig.isShowLog) {
                        System.out.println("toggle exception");
                        e2.printStackTrace();
                    }
                }
                return false;
            } catch (InvocationTargetException e) {
                if (BuildConfig.isShowLog) {
                    System.out.println("toggle exception");
                    e.printStackTrace();
                }
                return false;
            }
        return false;
    }

    /**
     * 绑定方法到事件监听中
     *
     * @param m
     * @param v
     * @param vi
     */
    private void bindListener(final Method m, View v, final Bind vi) {
        switch (vi.bindType()) {
            case CLICK:
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        invokeWithoutError(vi.runOnWorkThread(), m, v);
                    }
                });
                break;
            case LONG_CLICK:
                v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return invokeWithoutError(vi.runOnWorkThread(), m, v);
                    }
                });
                break;
            case ITEM_CLICK:
                ((AdapterView) v).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        invokeWithoutError(vi.runOnWorkThread(), m, parent, view, position, id);
                    }
                });
                break;
            case ITEM_LONG_CLICK:
                ((AdapterView) v).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        return invokeWithoutError(vi.runOnWorkThread(), m, parent, view, position, id);
                    }
                });
                break;
            default:
                break;
        }
    }

    /**
     * 绑定控件到方法和属性中
     */
    private void injectViewEvent() {
        Class cla = mHost.getClass();
        Class contentViewParent=null;

        if (cla.getAnnotation(ContentView.class) == null) {
            contentViewParent = Reflect.checkParentClassHadAnnotation(cla, ContentView.class);
        }
        //绑定控件方法.自身和有ContentView注解的父类
        injectMethod(cla.getDeclaredMethods());
        if(contentViewParent!=null) injectMethod(contentViewParent.getDeclaredMethods());

        //绑定视图到属性.自身和有ContentView注解的父类
        injectField(cla.getDeclaredFields());
        if(contentViewParent!=null) injectField(contentViewParent.getDeclaredFields());
    }

    private void injectMethod(Method[] methods) {
        if (methods != null && methods.length > 0) {
            for (final Method m : methods) {
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
}