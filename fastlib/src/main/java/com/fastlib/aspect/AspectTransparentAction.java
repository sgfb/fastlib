package com.fastlib.aspect;

import android.support.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by sgfb on 2020\02\21.
 * 透明切面事件.在原方法调用前后执行,行为不改变原方法逻辑包括错误.
 */
public interface AspectTransparentAction<T extends Annotation>{

    /**
     * 方法在调用前（包括不透明切面事件）响应
     * @param method        原方法
     * @param aspectActions 所有切面事件
     */
    void before(Object host,T anno,Method method,@Nullable List<Annotation> aspectActions) throws Throwable;

    /**
     * 方法调用后（包括不透明切面事件）
     * @param method    原方法
     * @param result    方法返回参
     * @param exception 方法或不透明切面事件弹出的异常
     */
    void after(Object host,T anno,Method method,@Nullable Object result,@Nullable Exception exception) throws Throwable;
}
