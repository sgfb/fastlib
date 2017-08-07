package com.fastlib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sgfb on 17/8/2.
 * 6.0运行权限注解.被注解的对象必须返回Runnable
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission{
    String[] value(); //请求的权限
    String print() default  ""; //简便的打印信息
    int type() default 0; //注解方法类型，0成功，其他失败
}