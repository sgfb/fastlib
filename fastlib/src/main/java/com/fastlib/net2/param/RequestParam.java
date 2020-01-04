package com.fastlib.net2.param;

import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 2019\12\24.
 * 网络请求参数容器
 */
public class RequestParam{
    private static ParamParserManager sParamParserManager;
    private Map<String,List<String>> mSurfaceParam=new HashMap<>();
    private List<Pair<String,Object>> mBottomParam=new ArrayList<>();

    static {
        sParamParserManager =new ParamParserManager();
        sParamParserManager.putParser(Integer.class,new IntParamParser());
        sParamParserManager.putParser(Long.class,new LongParamParser());
        sParamParserManager.putParser(Boolean.class,new BooleanParamParser());
        sParamParserManager.putParser(Float.class,new FloatParamParser());
        sParamParserManager.putParser(Double.class,new DoubleParamParser());
        sParamParserManager.putParser(String.class,new StringParamParser());
    }

    public void put(Object value){
        sParamParserManager.parser(false,this,null,value);
    }

    public void put(String key,Object value){
        sParamParserManager.parser(false,this,key,value);
    }

    public void add(Object value){
        sParamParserManager.parser(true,this,null,value);
    }

    public void add(String key,Object value){
        sParamParserManager.parser(true,this,key,value);
    }

    public Map<String, List<String>> getSurfaceParam() {
        return mSurfaceParam;
    }

    public List<Pair<String,Object>> getBottomParam() {
        return mBottomParam;
    }
}
