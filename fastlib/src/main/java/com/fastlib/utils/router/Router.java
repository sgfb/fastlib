package com.fastlib.utils.router;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Created by sgfb on 2020\02\12.
 */
public class Router {

    private Router(){}

    @SuppressWarnings("unchecked")
    public static <T> T gen(Class<T> cla){
        return (T) Proxy.newProxyInstance(cla.getClassLoader(), new Class[]{cla}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                ActivityRouter ar=method.getAnnotation(ActivityRouter.class);
                if(ar==null) throw new IllegalArgumentException("activity router no defined");

                Intent intent=new Intent((Context) args[0],ar.value());
                intent.putExtra("stub",0);
                Map<String,Object> map=getBundleMap(intent);
                for(int i=1;i<args.length;i++){
                    Object arg=args[i];
                    BundleArg bundleArg= (BundleArg) method.getParameterAnnotations()[i][0];
                    map.put(bundleArg.value(),arg);
                }
                intent.removeExtra("stub");
                ((Context)args[0]).startActivity(intent);
                return null;
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static Map<String,Object> getBundleMap(Intent intent){
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
}
