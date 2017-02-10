package com.fastlib.utils.json;

import android.content.Context;
import android.support.annotation.IdRes;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sgfb on 16/11/13.
 * json转换时留存的最小单元
 */
@SuppressWarnings("unchecked")
public final class JsonObject{
    private String mJsonRaw; //本单元的原始json字符串，可以进行二次json解析
    private String mKey; //json名
    private Object mValue; //json值

    public JsonObject(String raw,String key,Object value){
        mJsonRaw=raw;
        mKey=key;
        mValue=value;
    }

    /**
     * 根据键名返回值,将会逐层往下找,直到第一个或null返回
     * @param key
     * @param <T>
     * @throws ClassCastException
     * @return
     */
    public <T> T findValue(String key)throws ClassCastException{
        if(TextUtils.equals(mKey,key))
            return getValue();
        else{
            if(mValue instanceof Map){
                Map<String,JsonObject> joMap= (Map<String, JsonObject>) mValue;
                JsonObject jo=joMap.get(key);
                if(jo!=null)
                    return jo.findValue(key);
                else{
                    Iterator<String> iter=joMap.keySet().iterator();
                    while(iter.hasNext()){
                        T t=joMap.get(iter.next()).findValue(key);
                        return t;
                    }
                }
            }
        }
        throw new ClassCastException();
    }

    /**
     * 根据视图id返回值
     * @param context
     * @param id
     * @param <T>
     * @throws ClassCastException
     * @return
     */
    public <T> T findValue(Context context, @IdRes int id) throws ClassCastException{
        return findValue(context.getResources().getResourceEntryName(id));
    }

    /**
     * 获取值
     * @param <T>
     * @return
     * @throws ClassCastException
     */
    public <T> T getValue()throws ClassCastException{
        if(mValue==null)
            throw new ClassCastException();
        return (T) mValue;
    }

    /**
     * 类类型转换
     * @param cla
     * @param <T>
     * @return
     */
    public <T> T getValue(Class<?> cla){
        Gson gson=new Gson();
        try{
            return (T) gson.fromJson(mJsonRaw,cla);
        }catch (JsonParseException e){
            return null;
        }
    }

    /**
     * 特定类型转换
     * @param type
     * @param <T>
     * @return
     */
    public <T> T getValue(Type type){
        Gson gson=new Gson();
        try{
            return gson.fromJson(mJsonRaw,type);
        }catch (JsonParseException e){
            return null;
        }
    }
}