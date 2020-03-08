package com.fastlib.aspect.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sgfb on 2020\03\08.
 * 切面使用到的运行时参数
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AspectRuntimeArg{

    Class value();
}
