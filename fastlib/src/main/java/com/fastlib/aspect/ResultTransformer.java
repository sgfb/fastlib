package com.fastlib.aspect;

import com.fastlib.net2.utils.NetResultTransformer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sgfb on 2020\02\28.
 * 指定返回转换处理器
 * 方法和类两级支持,方法上注解优先
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResultTransformer{

    Class<? extends NetResultTransformer> value();
}
