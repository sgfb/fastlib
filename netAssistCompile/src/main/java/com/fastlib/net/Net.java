package com.fastlib.net;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sgfb on 18/4/21.
 * 标有次注解的类将在编译时生成网络接口辅助类.辅助类有以下几种用法
 * 1.继承接口辅助类,覆盖对应接口方法(以Callback结尾对应接口),调起对应接口后会自动回调
 * 2.实例化后调起对应接口并且给予监听回调
 * 3.实例化后直接获取Request使用
 * 4.实例化后直接获取Task(NetAction)使用
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Net {
}
