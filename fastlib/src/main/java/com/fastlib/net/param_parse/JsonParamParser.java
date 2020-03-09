package com.fastlib.net.param_parse;

import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.fastlib.net.Request;
import com.google.gson.Gson;

/**
 * Created by Administrator on 2018/5/16.
 * json化解析器
 */
public class JsonParamParser implements NetParamParser{

    @Override
    public boolean canParse(Request request,String key,Object obj){
        return !TextUtils.isEmpty(key)&&obj!=null;
    }

    @Override
    public boolean parseParam(boolean duplication, Request request, String key, Object obj){
        Gson gson=new Gson();
        String json=gson.toJson(obj);
        if(duplication) request.add(key,json);
        else request.put(key,json);
        return true;
    }

    @Override
    public int priority() {
        return 10;
    }
}