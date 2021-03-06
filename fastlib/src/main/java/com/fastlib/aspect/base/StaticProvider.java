package com.fastlib.aspect.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sgfb on 2020\02\27.
 * 静态环境提供器.
 * 被扫描到有这个注解的类将会加入到切面Activity中
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StaticProvider {
}
