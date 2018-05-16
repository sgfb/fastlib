package com.fastlib.bean;

import com.fastlib.annotation.NetBeanWrapper;

/**
 * Created by sgfb on 18/3/9.
 */
@NetBeanWrapper
public class InterfaceCheckTestBean{
    public int id;
    public String name;

    @Override
    public String toString() {
        return "id"+id+" name:"+name;
    }
}