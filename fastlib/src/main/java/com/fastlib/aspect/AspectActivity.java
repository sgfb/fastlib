package com.fastlib.aspect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.fastlib.base.RecyclerViewHolder;
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

    protected abstract void onReady();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocalDataInject =new LocalDataInject(this);
        initViewAndController();
        checkContentView();
    }

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
        if(mController instanceof BaseEnvironmentProvider){
            ((BaseEnvironmentProvider) mController).addEnvs(this);
            ((BaseEnvironmentProvider) mController).addEnvs(mActivityCallbackHolder);
            ((BaseEnvironmentProvider) mController).addEnvs(mPermissionResultReceiverGroup);
            ((BaseEnvironmentProvider) mController).addEnvs(new SimpleAspectCacheManager());
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
        ViewInject.inject(mView,rootView);
        EventObserver.getInstance().subscribe(this,this);
        mLocalDataInject.localDataInject();
        try {
            Method method = getClass().getDeclaredMethod("onReady");
            method.setAccessible(true);
            AspectSupport.callMethod(this,method);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
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
