package com.fastlib.net.param_parse;

import com.fastlib.annotation.NetBeanWrapper;
import com.fastlib.net.Request;

import java.lang.reflect.Field;

/**
 * Created by sgfb on 18/5/2.
 * 网络数据包裹类解析.字段名为键，字段值为值
 */
public class NetBeanWrapperParser implements NetParamParser{

    @Override
    public boolean canParse(Request request, String key, Object obj){
        if(obj==null) return false;
        NetBeanWrapper beanWrapper=obj.getClass().getAnnotation(NetBeanWrapper.class);
        return beanWrapper!=null;
    }

    @Override
    public boolean parseParam(boolean duplication, Request request, String key, Object obj){
        Field[] fields=obj.getClass().getDeclaredFields();

        try{
            for(Field field:fields){
                String realKey=field.getName();
                Object realValue=field.get(obj);

                if(duplication) request.add(realKey,realValue);
                else request.put(realKey,realValue);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public int priority() {
        return 0;
    }
}