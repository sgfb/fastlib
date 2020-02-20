package com.fastlib.aspect.exception;

import java.lang.annotation.Annotation;

/**
 * Created by sgfb on 2020\02\17.
 * 切面事件必要的环境参数不足时弹出
 */
public class EnvMissingException extends IllegalArgumentException{
    private Class<? extends Annotation> mCla;

    public EnvMissingException(Class<? extends Annotation> mCla) {
        this.mCla = mCla;
    }

    public Class<? extends Annotation> getCla() {
        return mCla;
    }
}
