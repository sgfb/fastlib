package com.fastlib.net.param_parse;

import com.fastlib.net.Request;

import java.util.Map;

/**
 * Created by sgfb on 18/5/1.
 * 默认非基本字段解析成网络请求参数
 */
public class MapParamParser implements NetParamParser {

    @Override
    public boolean canParse(Request request, String key, Object obj) {
        return obj instanceof Map;
    }

    @Override
    public boolean parseParam(boolean duplication,Request request, String key, Object obj){
        try{
            Map<String,String> map= (Map) obj;
            for(Map.Entry<String,String> entry:map.entrySet()){
                if(duplication) request.add(entry.getKey(),entry.getValue());
                else request.put(entry.getKey(),entry.getValue());
            }
        }catch (ClassCastException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}