package com.fastlib.net.param_parse;

import android.widget.TextView;

import com.fastlib.net.Request;

/**
 * Created by Administrator on 2018/5/16.
 */

public class TextViewParamParser extends ViewParamParser<TextView>{

    public TextViewParamParser(){
        super(TextView.class);
    }

    @Override
    public boolean parseParamAdapt(boolean duplication, Request request, String key, TextView obj){
        if(duplication) request.add(key,obj.getText());
        else request.put(key,obj.getText());
        return true;
    }
}
