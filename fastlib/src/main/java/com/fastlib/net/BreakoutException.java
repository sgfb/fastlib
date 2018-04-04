package com.fastlib.net;

/**
 * Created by sgfb on 18/1/23.
 * 中断异常
 */
public class BreakoutException extends NetException{

    public BreakoutException() {
        super("请求中断");
    }
}