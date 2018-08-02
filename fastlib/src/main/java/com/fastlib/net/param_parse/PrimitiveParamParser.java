package com.fastlib.net.param_parse;

import android.text.TextUtils;

import com.fastlib.net.Request;

/**
 * Created by sgfb on 2018/5/15.
 * 基本类型参数解析器
 */
public class PrimitiveParamParser implements NetParamParser{

    @Override
    public boolean canParse(Request request, String key, Object obj){
        return !TextUtils.isEmpty(key)&&isPrimitiveType(obj);
    }

    @Override
    public boolean parseParam(boolean duplication, Request request, String key, Object obj){
        Class cla=obj.getClass();
        if(duplication){
            if(cla==int.class) request.add(key,(int)obj);
            else if(cla==Integer.class) request.add(key,(int)obj);
            else if(cla==Float.class) request.add(key,(float)obj);
            else if(cla==Double.class) request.add(key,(double)obj);
            else if(cla==long.class) request.add(key,(long)obj);
            else if(cla==String.class) request.add(key,(String)obj);
            else if(cla==float.class) request.add(key,(float)obj);
            else if(cla==double.class) request.add(key,(double)obj);
            else if(cla==byte.class) request.add(key,(byte)obj);
            else if(cla==short.class) request.add(key,(short)obj);
            else if(cla==boolean.class) request.add(key,(boolean)obj);
            else if(cla==char.class) request.add(key,(char)obj);
            else if(cla==Byte.class) request.add(key,(byte)obj);
            else if(cla==Short.class) request.add(key,(short)obj);
            else if(cla==Long.class) request.add(key,(long)obj);
            else if(cla==Boolean.class) request.add(key,(boolean)obj);
            else if(cla==Character.class) request.add(key,(char)obj);
        }
        else{
            if(cla==int.class) request.put(key,(int)obj);
            else if(cla==Integer.class) request.put(key,(int)obj);
            else if(cla==Long.class) request.put(key,(long)obj);
            else if(cla==Float.class) request.put(key,(float)obj);
            else if(cla==Double.class) request.put(key,(double)obj);
            else if(cla==long.class) request.put(key,(long)obj);
            else if(cla==String.class) request.put(key,(String)obj);
            else if(cla==float.class) request.put(key,(float)obj);
            else if(cla==double.class) request.put(key,(double)obj);
            else if(cla==byte.class) request.put(key,(byte)obj);
            else if(cla==short.class) request.put(key,(short)obj);
            else if(cla==boolean.class) request.put(key,(boolean)obj);
            else if(cla==char.class) request.put(key,(char)obj);
            else if(cla==Byte.class) request.put(key,(byte)obj);
            else if(cla==Short.class) request.put(key,(short)obj);
            else if(cla==Boolean.class) request.put(key,(boolean)obj);
            else if(cla==Character.class) request.put(key,(char)obj);
        }
        return true;
    }

    @Override
    public int priority() {
        return -1;
    }

    private boolean isPrimitiveType(Object obj){
        Class cla=obj.getClass();
        return cla==byte.class||cla==short.class||cla==int.class||cla==long.class||
                cla==boolean.class||cla==float.class||cla==double.class||cla==char.class||
                cla==String.class||cla==Integer.class||cla==Byte.class||cla==Short.class||
                cla==Long.class||cla==Boolean.class||cla==Float.class||cla==Double.class||cla==Character.class;
    }
}
