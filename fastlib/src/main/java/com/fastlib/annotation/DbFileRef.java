package com.fastlib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Create by sgfb on 2019/04/18
 * E-Mail:602687446@qq.com
 * 数据库引用外部文件.这个注解对基本类型无效
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DbFileRef{
    String value() default "";
}
