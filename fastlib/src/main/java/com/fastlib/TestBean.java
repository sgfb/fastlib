package com.fastlib;

import com.fastlib.annotation.Database;
import com.fastlib.annotation.NetBeanWrapper;

/**
 * Created by sgfb on 2018/5/15.
 */
public class TestBean{
    @Database(keyPrimary = true)
    public String name;
    public int id;

    @Override
    public String toString() {
        return "TestBean{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}