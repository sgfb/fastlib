package com.fastlib.aspect.exception;

/**
 * Created by sgfb on 2020\02\17.
 * 切面事件必要的环境参数不足时弹出
 */
public class EnvMissingException extends IllegalArgumentException{

    public EnvMissingException() {
        super();
    }

    public EnvMissingException(String s) {
        super(s);
    }
}
