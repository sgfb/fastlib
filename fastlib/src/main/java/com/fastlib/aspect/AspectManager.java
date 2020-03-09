package com.fastlib.aspect;

import android.content.Context;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.fastlib.aspect.base.StaticProvider;
import com.fastlib.aspect.exception.EnvMissingException;
import com.fastlib.aspect.exception.ExceptionHandler;
import com.fastlib.utils.Reflect;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    private Map<Class, AspectTransparentAction> mTransparentAction;
    private Map<Class, AspectAction> mOpaqueAction;
    private Map<Class,Class> mStaticEnvs;
    private SparseArray<List<Object>> mRuntimeEnvs;        //临时寄存运行时参数
    private static AspectManager sInstance;
    private static final Object sLock = new Object();

    private AspectManager() {
        mTransparentAction = new HashMap<>();
        mOpaqueAction = new HashMap<>();
        mStaticEnvs=new HashMap<>();
        mRuntimeEnvs =new SparseArray<>();
    }

    public static AspectManager getInstance() {
        if (sInstance == null) {
            synchronized (sLock) {
                sInstance = new AspectManager();
            }
        }
        return sInstance;
    }

    public void init(Context context){
        addAspectActions(context.getApplicationContext(),"com.fastlib.aspect");
    }

    /**
     * 添加透明切面事件.注意调用者不能持有runnable对象否则可能造成内存泄漏
     */
    public void putTransparentAction(Class<? extends Annotation> cla, AspectTransparentAction aspect) {
        //替换
        Log.d(TAG,"添加透明切面事件:"+cla.getSimpleName()+"-->"+aspect.getClass().getSimpleName());
        mTransparentAction.put(cla, aspect);
    }

    /**
     * 添加不透明切面事件.注意调用者不能持有action对象否则可能造成内存泄漏
     */
    public void putOpaqueAction(Class<? extends Annotation> cla, AspectAction aspect) {
        //替换
        Log.d(TAG,"添加不透明切面事件:"+cla.getSimpleName()+"-->"+aspect.getClass().getSimpleName());
        mOpaqueAction.put(cla, aspect);
    }

    /**
     * 添加静态运行时参数.如果添加前有同等实现运行时参数将会被移除,例都实现了{@link com.fastlib.aspect.component.RuntimePermissionHandler}那么旧的那个将被移除
     * @param envCla    静态运行时参数类
     */
    public void addStaticEnv(Class envCla){
        Class[] interfaces=envCla.getInterfaces();
        if(interfaces==null||interfaces.length==0) Log.d(TAG,"不支持没用实现具体功能的环境类");
        else{
            for(Class inter:interfaces){
                Class oldStaticEnv=mStaticEnvs.get(inter);
                mStaticEnvs.put(inter,envCla);

                String addType=oldStaticEnv==null?"添加":"替换";
                String newEnvClaName=oldStaticEnv==null?envCla.getSimpleName():"("+oldStaticEnv.getSimpleName()+")"+envCla.getSimpleName();
                Log.d(TAG,String.format(Locale.getDefault(),"%s静态环境类:%s-->%s",addType,newEnvClaName,inter.getSimpleName()));
            }
        }
    }

    public Collection<Class> getStaticEnvs(){
        return mStaticEnvs.values();
    }

    public boolean checkAnnotationIsAction(Class<? extends Annotation> annotationCla){
        if(annotationCla==ThreadOn.class) return true;
        return mTransparentAction.containsKey(annotationCla)||mOpaqueAction.containsKey(annotationCla);
    }

    /**
     * 启动切面事件
     * 先直接启动透明事件
     * 后检查非透明事件,条件满足后才能启动
     */
    @SuppressWarnings("all")
    public Object callAction(Object o, List<Annotation> actionAnnotations, List envronment, Object[] args, MethodProxy proxyMethod) {
        Exception catchException=null;
        //填充全局运行时参数
        if(envronment==null) envronment=new ArrayList();
        Collection<Class> staticEnvs=getStaticEnvs();
        for(Class env:staticEnvs){
            try {
                envronment.add(env.newInstance());
            } catch (InstantiationException e) {
                catchException=e;
            } catch (IllegalAccessException e) {
                catchException=e;
            }
        }

        List<Annotation> opaqueActionAnnos = new ArrayList<>();
        List<Pair<Annotation,AspectTransparentAction>> transparentActions=new ArrayList<>();

        for (Annotation actionAnno : actionAnnotations) {
            //执行透明切面前段调用
            AspectTransparentAction transparentAction = mTransparentAction.get(actionAnno.annotationType());
            try{
                if (transparentAction != null){
                    transparentActions.add(Pair.create(actionAnno,transparentAction));
                    transparentAction.before(o,actionAnno,proxyMethod.getOriginalMethod(),actionAnnotations);
                }
                else opaqueActionAnnos.add(actionAnno);
            }catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        Object realResult=null;

        try {
            boolean success = true;
            Object delegateResult = null;
            List<MethodResultCallback> callbacks = new ArrayList<>();

            //检查和分类满足条件
            for (Annotation opaqueAnno : opaqueActionAnnos) {
                AspectAction action =mOpaqueAction.get(opaqueAnno.annotationType());
                if (action != null) {
                    ActionResult actionResult = action.handleAction(opaqueAnno, proxyMethod.getOriginalMethod(),envronment, args);
                    if (!actionResult.isPassed) {
                        //TODO 未通过处理
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
                    callback.onRawMethodResult(result);
                realResult=delegateResult != null ? delegateResult : result;
                return realResult;
            }
        } catch (Exception e) {
            if(e instanceof EnvMissingException)
                Log.w(TAG,e.getMessage());
            catchException=e;
        } finally {
            try{
                for(Pair<Annotation,AspectTransparentAction> pair:transparentActions)
                    pair.second.after(o,pair.first,proxyMethod.getOriginalMethod(),realResult,catchException);
            }catch (Throwable e){
                //透明事件不影响流程有异常仅显示不处理
                e.printStackTrace();
            }
        }
        if(catchException!=null){
            if(o instanceof ExceptionHandler)
                ((ExceptionHandler)o).onException(catchException);
        }
        return null;
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

                                StaticProvider staticProvider = (StaticProvider) cla.getAnnotation(StaticProvider.class);
                                if(staticProvider !=null){
                                    addStaticEnv(cla);
                                    continue;
                                }

                                boolean isTransparentAspect=false;
                                Class[] interfaces=cla.getInterfaces();
                                for(int i=0;i<interfaces.length;i++){
                                    Class claInterface=interfaces[i];
                                    if(claInterface==AspectTransparentAction.class){
                                        isTransparentAspect=true;
                                        Class aspectAnno= (Class) ((ParameterizedType)cla.getGenericInterfaces()[i]).getActualTypeArguments()[0];
                                        putTransparentAction(aspectAnno, (AspectTransparentAction) cla.newInstance());
                                        break;
                                    }
                                }
                                if(!isTransparentAspect&&Reflect.isExtendsFrom(cla,AspectAction.class)){
                                    //取出泛型指定的注解
                                    Type type= ((ParameterizedType)cla.getGenericSuperclass()).getActualTypeArguments()[0];
                                    if(type instanceof Class){
                                        Class aspectAnno= (Class) type;
                                        putOpaqueAction(aspectAnno, (AspectAction) cla.newInstance());
                                    }
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

    public void putRuntimeEnv(Object host, Object env){
        int hashCode=host.hashCode();
        List<Object> list= mRuntimeEnvs.get(hashCode);
        if(list==null){
            list=new ArrayList();
            mRuntimeEnvs.put(hashCode,list);
        }
        list.add(env);
    }

    public List<Object> getRuntimeEnvs(Object host){
        return mRuntimeEnvs.get(host.hashCode());
    }

    public void destroyRuntimeEnv(Object host){
        int hashCode=host.hashCode();
        List<Object> list= mRuntimeEnvs.get(hashCode);
        if(list!=null){
            mRuntimeEnvs.remove(hashCode);
            list.clear();
        }
    }
}
