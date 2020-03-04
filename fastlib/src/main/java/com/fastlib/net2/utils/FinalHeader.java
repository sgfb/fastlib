package com.fastlib.net2.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sgfb on 2020\03\04.
 * 网络请求头部常量
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface FinalHeader{
    /**
     * 多键值对以['key','value','key2','value2']形式组合,允许key重复
     */
    String[] value();
}
