package com.fastlib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 视图注解，使用在字段上填充字段。使用在方法上接受onClick回调
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Bind{
    String TYPE_CLICK ="click";
    String TYPE_LONG_CLICK ="longClick";

    int[] value(); //要绑定的视图id值

    String type() default TYPE_CLICK;
}