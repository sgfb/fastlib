package com.fastlib.net.exception;

/**
 * Created by sgfb on 18/1/23.
 * 中断异常
 */
public class BreakoutException extends NetException{

    public BreakoutException() {
        super("手动中断网络请求");
    }
}