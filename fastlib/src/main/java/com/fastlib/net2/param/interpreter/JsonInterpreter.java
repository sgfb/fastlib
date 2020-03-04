package com.fastlib.net2.param.interpreter;

import android.support.v4.util.Pair;

import com.fastlib.net2.param.RequestParam;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 2019\12\24.
 * 对请求参数做符合application/json形式的输出
 */
public class JsonInterpreter extends SingleInterpreter{

    @Override
    protected InputStream interpreterAdapter(RequestParam param){
        Map<String,List<String>> surfaceParam=param.getSurfaceParam();
        List<Pair<String,Object>> bottomParam=param.getBottomParam();
        Gson gson=new Gson();
        JsonObject root=new JsonObject();

        for(Map.Entry<String,List<String>> entry:surfaceParam.entrySet()){
            for(String value:entry.getValue()){
                root.addProperty(entry.getKey(),value);
            }
        }
        for(Pair<String,Object> pair:bottomParam){
            //TODO
            if(pair.first==null) return new ByteArrayInputStream(gson.toJson(pair.second).getBytes());

            root.add(pair.first,gson.toJsonTree(pair.second));
        }
        return new ByteArrayInputStream(gson.toJson(root).getBytes());
    }
}
