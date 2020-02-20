package com.fastlib.aspect.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sgfb on 2020\02\19.
 * 缓存原方法返回的数据,如果不为空之后返回的都是第一次的缓存
 * TODO 失效规则？
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache{

    /**
     * cache键名.为空的话使用方法签名
     */
    String value() default "";
}