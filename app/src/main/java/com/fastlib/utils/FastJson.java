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
}
