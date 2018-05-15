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
                field.setAccessible(true);
                String realKey=field.getName();

                if(duplication) {
                    if(field.getType()==int.class)
                        request.add(realKey,field.getInt(obj));
                    else if(field.getType()==float.class)
                        request.add(realKey,field.getFloat(obj));
                    else if(field.getType()==String.class){
                        String value= (String) field.get(obj);
                        if(value==null) continue;
                        request.add(realKey,value);
                    }
                }
                else{
                    if(field.getType()==int.class)
                        request.put(realKey,field.getInt(obj));
                    else if(field.getType()==float.class)
                        request.put(realKey,field.getFloat(obj));
                    else if(field.getType()==String.class){
                        String value= (String) field.get(obj);
                        if(value==null) continue;
                        request.put(realKey,value);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}