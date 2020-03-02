package com.fastlib.utils.fitout;

/**
 * Created by sgfb on 2020\03\02.
 * 实例生成
 */
public interface InstanceMaker<T>{

    T makeInstance(Class<T> cla);
}
