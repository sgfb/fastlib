package com.fastlib.utils;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 16/9/8.
 */
public class FastJson{

    private FastJson(){
        //no instance
    }

    public static Object fromJson(String json) throws IOException {
        return fromJsonReader(new JsonReader(new StringReader(json)));
    }

    public static Object fromJsonReader(JsonReader jsonReader) throws IOException {
        Object obj=null;
        JsonToken jt=jsonReader.peek();

        if(jt==JsonToken.BEGIN_ARRAY)
            obj=readJsonArray(jsonReader);
        else if(jt==JsonToken.BEGIN_OBJECT)
            obj=readJsonObj(jsonReader);
        jsonReader.close();
        return obj;
    }

    private static Object readJsonObj(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        Map<String,Object> map=new HashMap<>();
        JsonToken jt=jsonReader.peek();
        String name=null;

        if(jt==JsonToken.NULL){
            jsonReader.endObject();
            return null;
        }
        while(jsonReader.hasNext()){
            if(jt==JsonToken.NAME)
                name=jsonReader.nextName();
            else if(jt==JsonToken.BEGIN_OBJECT)
                map.put(name,readJsonObj(jsonReader));
            else if(jt==JsonToken.BEGIN_ARRAY)
                map.put(name,readJsonArray(jsonReader));
            else if(jt==JsonToken.BOOLEAN)
                map.put(name,jsonReader.nextBoolean());
            else if(jt==JsonToken.NUMBER){
                double temp=jsonReader.nextDouble();
                if(Math.abs(temp)>0&&Math.abs(temp)<1)
                    map.put(name,temp);
                else if(Math.abs(temp)>Integer.MAX_VALUE)
                    map.put(name,(long)temp);
                else
                    map.put(name,(int)temp);
            }
            else if(jt==JsonToken.STRING)
                map.put(name,jsonReader.nextString());
            else if(jt==JsonToken.NULL){
                jsonReader.nextNull();
                map.put(name,null);
            }
            else jsonReader.skipValue();
            jt=jsonReader.peek();
        }
        jsonReader.endObject();
        return map;
    }

    private static Object readJsonArray(JsonReader jsonReader) throws IOException{
        jsonReader.beginArray();
        List<Object> list=new ArrayList<>();
        JsonToken jt=jsonReader.peek();

        if(jt==JsonToken.NULL){
            jsonReader.endArray();
            return null;
        }
        while(jt!= JsonToken.END_ARRAY){
            if(jt==JsonToken.BEGIN_OBJECT)
                list.add(readJsonObj(jsonReader));
            else if(jt==JsonToken.BEGIN_ARRAY)
                list.add(readJsonArray(jsonReader));
            else if(jt==JsonToken.BOOLEAN)
                list.add(jsonReader.nextBoolean());
            else if(jt==JsonToken.NUMBER){
                double temp=jsonReader.nextDouble();
                if(Math.abs(temp)>0&&Math.abs(temp)<1)
                    list.add(temp);
                else if(Math.abs(temp)>Integer.MAX_VALUE)
                    list.add((long)temp);
                else
                    list.add((int)temp);
            }
            else if(jt==JsonToken.STRING)
                list.add(jsonReader.nextString());
            else if(jt==JsonToken.NULL)
                list.add(null);
            else jsonReader.skipValue();
            jt=jsonReader.peek();
        }
        jsonReader.endArray();
        return list;
    }

    /**
     * json解析后封装(辅助类)
     */
    public class JsonWrapper{
        private Object mRaw;

        public JsonWrapper(Object raw){
            mRaw=raw;
        }

        /**
         * 获取字符串.使用这个方法默认了json是map型的
         * @param name
         * @return
         */
        public String getString(String name,String def){
            if(mRaw instanceof Map<?,?>){
                Map<String,Object> map= (Map<String, Object>) mRaw;
                return (String)map.get(name);
            }
            return def;
        }

        /**
         * 获取整数.使用这个方法默认了json是map型的
         * @param name
         * @return
         */
        public int getInt(String name,int def){
            if(mRaw instanceof Map<?,?>){
                Map<String,Object> map= (Map<String, Object>) mRaw;
                return (int)map.get(name);
            }
            return def;
        }

        /**
         * 获取浮点数.使用这个方法默认了json是map型的
         * @param name
         * @param def
         * @return
         */
        public float getFloat(String name,float def){
            if(mRaw instanceof Map<?,?>){
                Map<String,Object> map= (Map<String, Object>) mRaw;
                return (float)map.get(name);
            }
            return def;
        }

        /**
         * 获取一个对象,这个对象必须有空构造.使用这个方法默认了json是map型的
         * @param name
         * @param cla
         * @param <T>
         * @return
         */
        public <T> T get(String name,Class<T> cla){
            try {
                T t=cla.newInstance();
                Map<String,Object> map= (Map<String, Object>) mRaw;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

        public Object getRaw(){
            return mRaw;
        }
    }
}
