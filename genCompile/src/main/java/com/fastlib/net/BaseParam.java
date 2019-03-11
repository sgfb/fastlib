package com.fastlib.net;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sgfb on 18/4/22.
 * 接口请求基本参数注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface BaseParam {
    String url();

    String method() default "post";

    /**
     * 自定义根地址
     * @return 根地址
     */
    String customRootAddress() default "";

    /**
     * 自定义Request
     * @return 自定义Request类
     */
    String customRequest() default "";
}
