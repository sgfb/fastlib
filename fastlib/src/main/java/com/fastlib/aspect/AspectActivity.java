package com.fastlib.aspect;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.fastlib.annotation.ContentView;
import com.fastlib.app.EventObserver;
import com.fastlib.aspect.component.ActivityResultReceiverGroup;
import com.fastlib.aspect.component.PermissionResultReceiverGroup;
import com.fastlib.aspect.component.SimpleAspectCacheManager;
import com.fastlib.base.OldViewHolder;
import com.fastlib.utils.Reflect;
import com.fastlib.utils.ViewInject;
import com.fastlib.utils.local_data.LocalDataInject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import leo.android.cglib.proxy.Enhancer;

/**
 * Created by sgfb on 2020\02\23.
 * 切面支持Activity
 * 分离出View和Controller
 * View层所有方法都执行在主线程中.拥有可选参数{@link OptionalComponent},拥有View注解功能{@link ViewInject}
 * Controller层可选择线程环境{@link ThreadOn}和各种切面事件,如{@link com.fastlib.annotation.Permission}
 */
public abstract class AspectActivity<V,C> extends AppCompatActivity{
    protected V mView;
    protected C mController;
    private LocalDataInject mLocalDataInject;
    private ActivityResultReceiverGroup mActivityCallbackHolder=new ActivityResultReceiverGroup();
    private PermissionResultReceiverGroup mPermissionResultReceiverGroup=new PermissionResultReceiverGroup();
    protected OldViewHolder mOldViewHolder;

    /**
     * 其他主要附件初始化完后最后调起的方法
     */
    protected abstract void onReady();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mLocalDataInject =new LocalDataInject(this);
        initViewAndController();
        checkContentView();
    }

    /**
     * 初始化View层和Controller层
     */
    @SuppressWarnings("unchecked")
    private void initViewAndController(){
        Type[] typeArgs=((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments();
        Enhancer enhancer=new Enhancer(this);

        enhancer.setSuperclass((Class<?>) typeArgs[0]);
        enhancer.setInterceptor(new MainThreadInvocation());
        mView= (V) enhancer.create();
        try {
            injectOptionalToView(genViewComponents());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        enhancer.setSuperclass((Class<?>) typeArgs[1]);
        enhancer.setInterceptor(new ControllerInvocationHandler());
        mController= (C) enhancer.create();
        if(mController instanceof AspectEnvironmentProvider){
            AspectEnvironmentProvider provider= (AspectEnvironmentProvider) mController;
            for(Class envCla:AspectManager.getInstance().getStaticEnvs()) {
                try {
                    provider.addEnvs(envCla.newInstance());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            provider.addEnvs(this);
            provider.addEnvs(mActivityCallbackHolder);
            provider.addEnvs(mPermissionResultReceiverGroup);
        }
    }

    protected List<Object> genViewComponents(){
        List<Object> list=new ArrayList<>();
        list.add(this);
        list.add(mOldViewHolder=new OldViewHolder());
        return list;
    }

    /**
     * 给View层注入可选参数
     */
    private void injectOptionalToView(List<Object> optionalParams) throws IllegalAccessException {
        Map<Class,Object> map=new HashMap<>();
        for(Object optional:optionalParams) {
            Class upClass=optional.getClass();
            while(upClass!=null){
                map.put(upClass, optional);
                upClass=upClass.getSuperclass();
            }
        }

        Class viewCla=mView.getClass();
        //View层是代理类所以遍历的是父类的字段,目前不考虑View层继承
        for(Field field :viewCla.getSuperclass().getDeclaredFields()){
            OptionalComponent optionalComponent=field.getAnnotation(OptionalComponent.class);
            if(optionalComponent!=null&&map.containsKey(field.getType())){
                field.setAccessible(true);
                field.set(mView,map.get(field.getType()));
            }
        }
    }

    private void checkContentView(){
        ContentView cv = Reflect.findAnnotation(getClass(),ContentView.class);
        if(cv!=null)
            setContentView(cv.value());
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        afterSetContentView();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        afterSetContentView();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        afterSetContentView();
    }

    private void afterSetContentView(){
        View rootView=findViewById(android.R.id.content);
        mOldViewHolder.setRootView(rootView);
        ViewInject.inject(this,rootView);
        ViewInject.inject(mView,rootView,mView.getClass().getSuperclass());
        EventObserver.getInstance().subscribe(this,this);
        mLocalDataInject.localDataInject();

        //依次初始化View,Controller,Activity
        List<Method> initMethods=new ArrayList<>();
        initMethods.addAll(genInitMethods(mView.getClass().getSuperclass()));
        initMethods.addAll(genInitMethods(mController.getClass().getSuperclass()));
        initMethods.addAll(getSelfInitMethods());
        for(Method method:initMethods){
            AspectSupport.callMethod(this,method);
        }
    }

    private List<Method> getSelfInitMethods(){
        List<Method> methods=new ArrayList<>();
        try {
            Method mainInitMathod = getClass().getDeclaredMethod("onReady");
            mainInitMathod.setAccessible(true);
            methods.add(mainInitMathod);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        methods.addAll(genInitMethods(getClass()));
        return methods;
    }

    private List<Method> genInitMethods(Class cla){
        List<Method> methods=new ArrayList<>();

        Method[] selfMethods=cla.getDeclaredMethods();
        for(Method m:selfMethods){
            if(m.getAnnotation(OptionalInit.class)!=null){
                m.setAccessible(true);
                methods.add(m);
            }
        }
        return methods;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventObserver.getInstance().unsubscribe(this,this);
        if(mController instanceof BaseEnvironmentProvider)
            ((BaseEnvironmentProvider) mController).environmentDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mActivityCallbackHolder.sendEvent(requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionResultReceiverGroup.sendEvent(requestCode,permissions,grantResults);
    }
}
