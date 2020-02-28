package com.fastlib.aspect.component;

import com.fastlib.aspect.ThreadOn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sgfb on 2020\02\27.
 * 延迟触发某个事件.因为延迟可能无限长所以不能加入线程池(以免造成阻塞),而是加入线程队列
 */
@ThreadOn(ThreadOn.ThreadType.WORK)
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Delay{

    /**
     * 延迟时长.毫秒单位
     */
    long value();
}
