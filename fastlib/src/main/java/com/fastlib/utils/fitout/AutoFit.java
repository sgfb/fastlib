package com.fastlib.utils.fitout;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sgfb on 2020\03\02.
 * 自动装配
 * 如果attachment为空实例化注解对应字段,不为空则实例化
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFit{

    Class attachment()default void.class;
}
