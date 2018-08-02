package com.fastlib.net.param_parse;

import android.widget.Spinner;

import com.fastlib.net.Request;

/**
 * Created by Administrator on 2018/5/16.
 */

public class SpinnerParamParser extends ViewParamParser<Spinner>{

    public SpinnerParamParser() {
        super(Spinner.class);
    }

    @Override
    public boolean parseParamAdapt(boolean duplication, Request request, String key, Spinner view){
        if(duplication) request.add(key,(String) view.getSelectedItem());
        else request.put(key,(String)view.getSelectedItem());
        return true;
    }
}
