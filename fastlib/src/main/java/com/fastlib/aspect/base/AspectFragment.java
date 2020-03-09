package com.fastlib.aspect.base;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fastlib.annotation.ContentView;
import com.fastlib.app.EventObserver;
import com.fastlib.aspect.AspectManager;
import com.fastlib.aspect.AspectSupport;
import com.fastlib.aspect.component.ActivityResultReceiverGroup;
import com.fastlib.aspect.component.PermissionResultReceiverGroup;
import com.fastlib.base.OldViewHolder;
import com.fastlib.utils.Reflect;
import com.fastlib.utils.bind_view.ViewInject;
import com.fastlib.utils.fitout.FitoutFactory;
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
 * Created by sgfb on 2020\02\28.
 */
public abstract class AspectFragment<V,C> extends Fragment {
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocalDataInject =new LocalDataInject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        initViewAndController();
        return genContentView(inflater);
    }

    private View genContentView(LayoutInflater layoutInflater){
        ContentView cv = Reflect.findAnnotation(getClass(),ContentView.class);
        if(cv!=null)
            return layoutInflater.inflate(cv.value(),null);
        return null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        afterSetContentView();
    }

    /**
     * 初始化View层和Controller层
     */
    @SuppressWarnings("unchecked")
    private void initViewAndController(){
        Type[] typeArgs=((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments();
        Enhancer enhancer=new Enhancer(getContext());

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
        AspectManager.getInstance().putRuntimeEnv(mController,this);
        AspectManager.getInstance().putRuntimeEnv(mController,mActivityCallbackHolder);
        AspectManager.getInstance().putRuntimeEnv(mController,mPermissionResultReceiverGroup);
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

    private void afterSetContentView(){
        View rootView=getView();
        ViewInject.inject(mView,rootView,mView.getClass().getSuperclass());

        try {
            FitoutFactory.autoFitout(mView,mView.getClass().getSuperclass());
            FitoutFactory.autoFitout(mController,mController.getClass().getSuperclass());
            FitoutFactory.autoFitout(this);
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        ViewInject.inject(this,rootView);
        EventObserver.getInstance().subscribe(getContext(),this);
        mOldViewHolder.setRootView(rootView);
        mLocalDataInject.localDataInject();

        for(Method method:genInitMethods(mView.getClass().getSuperclass()))
            AspectSupport.callMethod(mView,method);
        for(Method method:genInitMethods(mController.getClass().getSuperclass()))
            AspectSupport.callMethod(mController,method);
        for(Method method:getSelfInitMethods())
            AspectSupport.callMethod(this,method);
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
    public void onDestroy() {
        super.onDestroy();
        EventObserver.getInstance().unsubscribe(getContext(),this);
        AspectManager.getInstance().destroyRuntimeEnv(mController);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mActivityCallbackHolder.sendEvent(requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionResultReceiverGroup.sendEvent(requestCode,permissions,grantResults);
    }
}
