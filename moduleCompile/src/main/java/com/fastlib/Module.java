package com.fastlib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sgfb on 2018/7/24.
 * 模块注解.只有加了这个注解才支持fastlib专用路由跳转器
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Module{
    String value();
    String group() default "";
}
