package com.fastlib.aspect.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sgfb on 2020\02\27.
 * {@link AspectActivity}可选初始化方法.分离不同初始化事件,在异常弹出时互不影响
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OptionalInit {
}
