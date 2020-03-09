package com.fastlib.net.param_parse;

import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.fastlib.net.Request;

/**
 * Created by Administrator on 2018/5/16.
 */
public abstract class ViewParamParser<T extends View> implements NetParamParser{
    private Class<T> mCla;

    public ViewParamParser(@NonNull Class<T> mCla) {
        this.mCla = mCla;
    }

    public abstract boolean parseParamAdapt(boolean duplication, Request request, String key, T view);

    @Override
    public boolean canParse(Request request, String key, Object obj){
        return !TextUtils.isEmpty(key)&&mCla.isInstance(obj);
    }

    @Override
    public boolean parseParam(boolean duplication, Request request, String key, Object obj){
        return parseParamAdapt(duplication,request,key, (T) obj);
    }

    @Override
    public int priority() {
        return 0;
    }
}