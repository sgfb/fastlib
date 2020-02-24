package com.fastlib.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sgfb on 2020\02\24.
 * 可选组件
 * 对某些类（目前是切面组件）有一些可选参数可以使用此注解要求持有方给出,但不一定能给出需要自行确认
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OptionalComponent {
}
