package com.fastlib.utils.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Created by sgfb on 2020\02\12.
 * 路由辅助
 */
public abstract class Router<H,T>{
    private T mRouterLink;
    protected H mHost;

    protected Router(Class<T> routeLinkCla, H mHost) {
        this.mHost = mHost;
        mRouterLink=gen(routeLinkCla);
    }

    @SuppressWarnings("unchecked")
    public static <T> Router<Activity,T> createRouter(Class<T> linkCla,Activity activity){
        return new ActivityRouter(linkCla,activity);
    }

    @SuppressWarnings("unchecked")
    public static <T> Router<Fragment,T> createRouter(Class<T> linkCla,Fragment fragment){
        return new FragmentRouter(linkCla,fragment);
    }

    public void bindClick2StartActivity(@IdRes int viewId,final Class<? extends Activity> activityCla, final RouterArg... args){
        bindClick2StartActivity(viewId,-1,activityCla,args);
    }

    /**
     * 绑定点击事件启动Activity
     * @param viewId            绑定点击对象View的id
     * @param requestCode   如果需要返回指定code否则为-1
     * @param activityCla   启动的Activity类
     * @param args          启动Activity时传的参数
     */
    public void bindClick2StartActivity(@IdRes int viewId, final int requestCode, final Class<? extends Activity> activityCla, final RouterArg... args){
        View view=findViewById(viewId);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),activityCla);
                intent.putExtra("stub",0);
                Map<String,Object> argMap=getBundleMap(intent);
                if(args!=null){
                    for(RouterArg arg:args){
                        argMap.put(arg.name,arg.arg);
                    }
                }
                intent.removeExtra("stub");
                if(requestCode==-1)
                    startActivity(intent);
                else startActivityForResult(intent,requestCode);
            }
        });
    }

    protected abstract View findViewById(@IdRes int id);

    protected abstract Context getContext();

    protected abstract void startActivity(Intent intent);

    protected abstract void startActivityForResult(Intent intent,int requestCode);

    @SuppressWarnings("unchecked")
    private T gen(Class<T> cla){
        return (T) Proxy.newProxyInstance(cla.getClassLoader(), new Class[]{cla}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                ActivityPath ar=method.getAnnotation(ActivityPath.class);
                if(ar==null) throw new IllegalArgumentException("activity router no defined");

                Intent intent=new Intent(getContext(),ar.value());
                intent.putExtra("stub",0);
                Map<String,Object> map=getBundleMap(intent);
                for(int i=0;i<args.length;i++){
                    Object arg=args[i];
                    Arg bundleArg= (Arg) method.getParameterAnnotations()[i][0];
                    map.put(bundleArg.value(),arg);
                }
                intent.removeExtra("stub");
                startActivity(intent);
                return null;
            }
        });
    }

    @SuppressWarnings("unchecked")
    private @NonNull Map<String,Object> getBundleMap(Intent intent){
        try {
            Field extrasField=Intent.class.getDeclaredField("mExtras");
            extrasField.setAccessible(true);
            Bundle extras= (Bundle) extrasField.get(intent);

            Field mapField=Bundle.class.getDeclaredField("mMap");
            mapField.setAccessible(true);
            return (Map<String, Object>) mapField.get(extras);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public T getRouterLink(){
        return mRouterLink;
    }
}
