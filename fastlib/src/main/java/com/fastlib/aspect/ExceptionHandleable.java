package com.fastlib.aspect;

/**
 * Created by sgfb on 2020\02\23.
 * 指定可处理的异常类型
 */
public @interface ExceptionHandleable{

    Class[] value();
}
