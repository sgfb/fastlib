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
 */
@SuppressWarnings("unchecked")
public final class JsonObject{
    private String mJsonRaw;
    private String mKey;
    private Object mValue;

    public JsonObject(String raw,String key,Object value){
        mJsonRaw=raw;
        mKey=key;
        mValue=value;
    }

    /**
     * 根据键名返回值
     * @param key
     * @param <T>
     * @return
     */
    public <T> T findValue(String key){
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
                        if(t!=null)
                            return t;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据视图id返回值
     * @param context
     * @param id
     * @param <T>
     * @return
     */
    public <T> T findValue(Context context, @IdRes int id){
        return findValue(context.getResources().getResourceEntryName(id));
    }

    public <T> T getValue(){
        if(mValue==null)
            return null;
        return (T) mValue;
    }

    public <T> T getValue(Class<?> cla){
        Gson gson=new Gson();
        try{
            return (T) gson.fromJson(mJsonRaw,cla);
        }catch (JsonParseException e){
            return null;
        }
    }

    public <T> T getValue(Type type){
        Gson gson=new Gson();
        try{
            return gson.fromJson(mJsonRaw,type);
        }catch (JsonParseException e){
            return null;
        }
    }
}