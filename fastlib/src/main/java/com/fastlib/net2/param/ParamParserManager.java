package com.fastlib.net2.param;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 2019\12\18.
 * 参数解析器管理
 */
public class ParamParserManager{
    private Map<Class,ParamParser> mMap=new HashMap<>();

    public <T> void putParser(Class<T> cla,ParamParser<T> parser){
        mMap.put(cla,parser);
    }

    public void removeParser(Class cla){
        mMap.remove(cla);
    }

    @SuppressWarnings("unchecked")
    public void parser(boolean duplication, RequestParam requestParams,@Nullable String key, @NonNull Object value){
        ParamParser parser=mMap.get(value.getClass());
        if(parser==null){
            requestParams.getBottomParam().add(Pair.create(key,value));
            return;
        }

        Map<String,List<String>> convertParam=parser.parseParam(key,value);
        if(convertParam==null||convertParam.isEmpty())
            return;
        if(duplication){
            for(Map.Entry<String,List<String>> entry:convertParam.entrySet()){
                safeCheckEmptyList(requestParams,entry.getKey());
                for(String str:entry.getValue()){
                    requestParams.getSurfaceParam().get(entry.getKey()).add(str);
                }
            }
        }
        else{
            for(Map.Entry<String,List<String>> entry:convertParam.entrySet()){
                safeCheckEmptyList(requestParams,entry.getKey());
                Map<String,List<String>> surfaceParam=requestParams.getSurfaceParam();
                surfaceParam.get(entry.getKey()).clear();
                if(!entry.getValue().isEmpty())
                    surfaceParam.get(entry.getKey()).add(entry.getValue().get(entry.getValue().size()-1));
            }
        }
    }

    private void safeCheckEmptyList(RequestParam requestParam, String key){
        Map<String,List<String>> surfaceParam=requestParam.getSurfaceParam();
        if(!surfaceParam.containsKey(key))
            surfaceParam.put(key,new ArrayList<String>());
    }
}
