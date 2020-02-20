package com.fastlib.aspect;

/**
 * Created by sgfb on 2020\02\17.
 * 原方法结果返回回调
 * 这个类用于检查所有切面事件是否都通过,只有都通过的情况下才全部回调
 */
public interface MethodResultCallback{

    void onCheckedSuccess(Object result);
}
