package com.fastlib.utils;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.LocalData;
import com.fastlib.app.GlobalConfig;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by sgfb on 17/1/12.
 * 视图注入到字段和方法中
 */
public class ViewInject{
    private ThreadPoolExecutor mThreadPool;
    private Activity mActivity;
    private Fragment mFragment;

    private ViewInject( Activity activity, Fragment fragment,ThreadPoolExecutor threadPool){
        mThreadPool = threadPool;
        mActivity = activity;
        mFragment = fragment;
        injectViewEvent();
    }

    public static void inject(Activity activity){
        ViewInject vi=new ViewInject(activity,null,null);
    }

    public static void inject(Fragment fragment){
        ViewInject vi=new ViewInject(null,fragment,null);
    }

    public static void inject(Activity activity,ThreadPoolExecutor threadPool){
        ViewInject vi=new ViewInject(activity,null,threadPool);
    }

    public static void inject(Fragment fragment,ThreadPoolExecutor threadPool){
        ViewInject vi=new ViewInject(null,fragment,threadPool);
    }

    /**
     * 运行一段代码如果有异常自行处理
     * @param runOnWorkThread
     * @param m
     * @param objs
     */
    private boolean invokeWithoutError(boolean runOnWorkThread, final Method m, final Object... objs){
        if(runOnWorkThread&&mThreadPool!=null)
            mThreadPool.execute(new Runnable() {
                @Override
                public void run(){
                    try {
                        m.invoke(getReceiver(),objs);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            });
        else
            try {
                Object result=m.invoke(getReceiver(),objs);
                if(result instanceof Boolean)
                    return (Boolean)result;
            } catch (IllegalAccessException e){
                if(GlobalConfig.SHOW_LOG)
                System.out.println("toggle error:"+e.getCause());
                return false;
            } catch (InvocationTargetException e){
                if(GlobalConfig.SHOW_LOG)
                System.out.println("toggle error:"+e.getCause());
                return false;
            }
        return false;
    }

    /**
     * 绑定方法到事件监听中
     * @param m
     * @param v
     * @param vi
     */
    private void bindListener(final Method m, View v, final Bind vi){
        switch(vi.bindType()){
            case CLICK:
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v){
                        invokeWithoutError(vi.runOnWorkThread(),m,v);
                    }
                });
                break;
            case LONG_CLICK:
                v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v){
                        return invokeWithoutError(vi.runOnWorkThread(),m,v);
                    }
                });
                break;
            case ITEM_CLICK:
                ((AdapterView)v).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        invokeWithoutError(vi.runOnWorkThread(),m,parent,view,position,id);
                    }
                });
                break;
            case ITEM_LONG_CLICK:
                ((AdapterView)v).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        return invokeWithoutError(vi.runOnWorkThread(),m,parent,view,position,id);
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
    private void injectViewEvent(){
        //绑定控件方法
        Method[] methods=getReceiver().getClass().getDeclaredMethods();
        if(methods!=null&&methods.length>0)
            for(final Method m:methods){
                m.setAccessible(true);
                Bind vi=m.getAnnotation(Bind.class);
                LocalData ld=m.getAnnotation(LocalData.class);
                if(vi!=null&&ld==null){
                    int[] ids=vi.value();
                    if(ids.length>0){
                        for(int id:ids){
                            View v=findViewById(id);
                            if(v!=null)
                                bindListener(m,v,vi);
                        }
                    }
                }
            }

        //绑定视图到属性
        Field[] fields=getReceiver().getClass().getDeclaredFields();
        if(fields!=null&&fields.length>0)
            for(Field field:fields){
                Bind vi=field.getAnnotation(Bind.class);
                if(vi!=null){
                    int[] ids=vi.value();
                    if(ids.length>0){
                        try {
                            field.setAccessible(true);
                            field.set(getReceiver(),findViewById(ids[0]));
                        } catch (IllegalAccessException e) {
                            if(GlobalConfig.SHOW_LOG)
                            System.out.println(e.getMessage());
                        }
                    }
                }
            }
    }

    private View findViewById(int id){
        if(mActivity!=null)
            return mActivity.findViewById(id);
        if(mFragment.getView()!=null)
            return mFragment.getView().findViewById(id);
        return null;
    }

    private Object getReceiver(){
        if(mActivity!=null)
            return mActivity;
        else
            return mFragment;
    }
}
