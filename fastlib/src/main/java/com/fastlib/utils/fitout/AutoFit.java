package com.fastlib.utils.fitout;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sgfb on 2020\03\02.
 * 自动装配.对参数实例化不使用new赋值,而是统一使用从指定生成方法获取
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFit{
}
