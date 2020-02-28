package com.fastlib.net2.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sgfb on 2020\02\27.
 * 网络请求常量参数
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface FinalParam{

    /**
     * 多键值对以['key','value','key2','value2']形式组合
     */
    String[] value();
}
