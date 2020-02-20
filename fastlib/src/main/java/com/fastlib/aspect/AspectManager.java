package com.fastlib.aspect;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.fastlib.aspect.exception.EnvMissingException;
import com.fastlib.utils.Reflect;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dalvik.system.DexFile;
import leo.android.cglib.proxy.MethodProxy;

/**
 * Created by sgfb on 2020\02\16.
 * 切面事件管理.负责添加和删除切面事件,为使用者使用切面事件搭建桥梁
 * TODO {@link ActionResult}缓存优化
 */
public class AspectManager {
    public static final String TAG = AspectManager.class.getSimpleName();

    /**
     * 透明事件指不改变原方法流向的切面事件,例如日志保存或输出.
     * 非透明事件将改变原方法流向,可能最终不会调起原方法或者调起多次,
     * 原则上在多非透明事件同时触发情况下如果有未通过的事件则不调起原方法,都通过则调起最多次的次数,
     * 例如权限事件未通过则不应调起原方法
     */
    private Map<Class, Runnable> mTransparentAction;
    private Map<Class, AspectAction> mOpaqueAction;
    private static AspectManager sInstance;
    private static final Object sLock = new Object();

    private AspectManager() {
        mTransparentAction = new HashMap<>();
        mOpaqueAction = new HashMap<>();
    }

    public static AspectManager getInstance() {
        if (sInstance == null) {
            synchronized (sLock) {
                sInstance = new AspectManager();
            }
        }
        return sInstance;
    }

    /**
     * 添加透明切面事件.注意调用者不能持有runnable对象否则可能造成内存泄漏
     */
    public void putTransparentAction(Class<? extends Annotation> cla, Runnable runnable) {
        mTransparentAction.put(cla, runnable);
    }

    /**
     * 添加不透明切面事件.注意调用者不能持有action对象否则可能造成内存泄漏
     */
    public void putOpaqueAction(Class<? extends Annotation> cla, AspectAction action) {
        Log.d(TAG,"添加不透明切面事件:"+cla.getSimpleName()+","+action.getClass().getSimpleName());
        mOpaqueAction.put(cla, action);
    }

    /**
     * 启动切面事件
     * 先直接启动透明事件
     * 后检查非透明事件,条件满足后才能启动
     */
    public Object callAction(Object o, List<Annotation> actionAnnotations, List envronment, Object[] args, MethodProxy proxyMethod) {
        ArrayList<Annotation> allAnnotation = new ArrayList<>();
        List<Annotation> opaqueActionAnnos = new ArrayList<>();

        flatAnnotation(allAnnotation, actionAnnotations);
        for (Annotation actionAnno : allAnnotation) {
            Runnable transparentAction = mTransparentAction.get(actionAnno.annotationType());
            if (transparentAction != null) transparentAction.run();
            else opaqueActionAnnos.add(actionAnno);
        }

        try {
            boolean success = true;
            Object delegateResult = null;
            List<MethodResultCallback> callbacks = new ArrayList<>();

            //检查和分类满足条件
            for (Annotation opaqueAnno : opaqueActionAnnos) {
                AspectAction action = mOpaqueAction.get(opaqueAnno.annotationType());
                if (action != null) {
                    ActionResult actionResult = action.handleAction(opaqueAnno, envronment, args);
                    if (!actionResult.isPassed) {
                        //TODO 错误处理
                        success = false;
                        break;
                    }
                    if (actionResult.rawResultCallback != null)
                        callbacks.add(actionResult.rawResultCallback);
                    if (actionResult.result != null)
                        delegateResult = actionResult.result;
                } else success = false;
            }
            if (success) {
                Object result = proxyMethod.invokeSuper(o, args);
                for (MethodResultCallback callback : callbacks)
                    callback.onCheckedSuccess(result);
                return delegateResult != null ? delegateResult : result;
            }
        } catch (EnvMissingException e) {
            Log.w(TAG, "注解环境缺失:" + e.getCla());
        }
        return null;
    }

    /**
     * 平铺注解.将注解的注解拿出来填充到注解的前面
     */
    private void flatAnnotation(List<Annotation> flatList, List<Annotation> list) {
        for (Annotation annotation : list) {
            if (flatList.contains(annotation) || (mTransparentAction.get(annotation.annotationType()) == null
                    && mOpaqueAction.get(annotation.annotationType()) == null)) continue;
            flatList.add(0, annotation);
            if (!list.isEmpty())
                flatAnnotation(flatList, Arrays.asList(annotation.annotationType().getAnnotations()));
        }
    }

    @SuppressWarnings("unchecked")
    public void addAspectActions(Context context, @Nullable String filterPackageName) {
        try {
            File file = new File(context.getPackageCodePath());
            List<String> pathList=new ArrayList<>();

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                File packageCodeParent = file.getParentFile();
                if(packageCodeParent!=null) {
                    for(File childFile:packageCodeParent.listFiles()){
                        String childName=childFile.getName();
                        if (childName.endsWith(".apk") && childName.contains("slice"))
                            pathList.add(childFile.getAbsolutePath());
                    }
                }
                else Log.w(TAG, "类包父路径不存在");
            }
            else pathList.add(file.getAbsolutePath());

            int classesCount = 0;
            long timer = System.currentTimeMillis();
            for (String childPath :pathList) {
                DexFile df = new DexFile(childPath);
                Enumeration<String> e = df.entries();
                while (e.hasMoreElements()) {
                    String className = e.nextElement();

                    try {
                        if (!TextUtils.isEmpty(filterPackageName)) {
                            if (className.contains(filterPackageName)) {
                                Class cla = Class.forName(className);
                                if(Reflect.isExtendsFrom(cla,AspectAction.class)){
                                    //取出泛型指定的注解
                                    Class aspectAnno= (Class) ((ParameterizedType)cla.getGenericSuperclass()).getActualTypeArguments()[0];
                                    putOpaqueAction(aspectAnno, (AspectAction) cla.newInstance());
                                }
                            }
                        }
                    } catch (ClassNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                    } catch (InstantiationException e1) {
                        e1.printStackTrace();
                    }
                    classesCount++;
                }
            }
            Log.d(TAG, "遍历类数量:" + classesCount + " 耗时:" + (System.currentTimeMillis() - timer)+"ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
