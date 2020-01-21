package com.fastlib.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sgfb on 2020\01\07.
 * 切面编程使用的注解.标识方法运行线程环境
 */
@Target({ElementType.METHOD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ThreadOn{

    ThreadType value();

    enum ThreadType{
        MAIN,           //主线程
        WORK,           //工作线程.如果调用点已经是非主线程则不开启新线程
    }
}
