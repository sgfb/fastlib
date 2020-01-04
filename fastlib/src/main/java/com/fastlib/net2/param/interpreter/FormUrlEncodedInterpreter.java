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
 * 将参数解释为符合x-www-urlencoded规则的输出
 */
public class FormUrlEncodedInterpreter extends SingleInterpreter {

    @Override
    protected InputStream interpreterAdapter(RequestParam param) {
        StringBuilder sb=new StringBuilder();
        Map<String,List<String>> surfaceParam=param.getSurfaceParam();

        for(Map.Entry<String,List<String>> entry:surfaceParam.entrySet()){
            for(String value:entry.getValue()){
                try {
                    String encodedKey=URLEncoder.encode(entry.getKey(),"UTF-8");
                    String encodedValue=URLEncoder.encode(value,"UTF-8");
                    sb.append(encodedKey).append('=').append(encodedValue).append('&');
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        if(sb.length()>0)
            sb.deleteCharAt(sb.length()-1);
        return new ByteArrayInputStream(sb.toString().getBytes());
    }
}
