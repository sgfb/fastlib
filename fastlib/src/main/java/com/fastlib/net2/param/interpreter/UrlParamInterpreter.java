package com.fastlib.net2.param.interpreter;

import com.fastlib.net2.param.RequestParam;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 2019\12\24.
 * Url参数解释器
 */
public class UrlParamInterpreter extends SingleInterpreter {

    @Override
    protected InputStream interpreterAdapter(RequestParam param) {
        StringBuilder sb=new StringBuilder();

        sb.append('?');
        for(Map.Entry<String,List<String>> entry:param.getSurfaceParam().entrySet()){
            for(String value:entry.getValue()){
                try{
                    sb.append(URLEncoder.encode(entry.getKey(),"UTF-8")).append('=').append(URLEncoder.encode(value,"UTF-8")).append('&');
                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }
            }
        }
        sb.deleteCharAt(sb.length()-1);
        return new ByteArrayInputStream(sb.toString().getBytes());
    }
}
