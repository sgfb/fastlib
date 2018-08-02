package com.fastlib.net.param_parse;

import android.support.annotation.NonNull;

import com.fastlib.net.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by sgfb on 18/5/2.
 * 参数解析管理器
 */
public class ParamParserManager{
    Map<NetParamParserClass,NetParamParser> mNetParamParserMap =new TreeMap<>();

    public void putParser(NetParamParser paramParser){
        mNetParamParserMap.put(new NetParamParserClass(paramParser.priority(),paramParser.getClass()),paramParser);
    }

    public void removeParser(NetParamParser paramParser){
        mNetParamParserMap.remove(new NetParamParserClass(paramParser.priority(),paramParser.getClass()));
    }

    public void parserParam(boolean duplication,Request request,String key,Object obj){

        for(Map.Entry<NetParamParserClass,NetParamParser> entry: mNetParamParserMap.entrySet()){
            NetParamParser paramParser=entry.getValue();
            if(paramParser.canParse(request,key,obj)&&paramParser.parseParam(duplication,request,key,obj)){
                break;
            }
        }
    }

    class NetParamParserClass implements Comparable<NetParamParserClass>{
        int mPriority;
        Class<? extends NetParamParser> mCla;

        public NetParamParserClass(int mPriority, Class<? extends NetParamParser> mCla) {
            this.mPriority = mPriority;
            this.mCla = mCla;
        }

        @Override
        public int compareTo(@NonNull NetParamParserClass another) {
            if(mPriority==another.mPriority) return 0;
            if(mPriority<another.mPriority) return -1;
            else return 1;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof NetParamParserClass&&((NetParamParserClass)o).mCla==mCla;
        }
    }
}