package com.fastlib;

import com.fastlib.annotation.DatabaseInject;

/**
 * Created by sgfb on 16/9/13.
 */
public class Bean{
    @DatabaseInject(keyPrimary =true,autoincrement = true)
    public int id;
    public String name;
    @DatabaseInject(ignore = true)
    public int extraField;

    @Override
    public String toString(){
        return "id:"+id+" name:"+name+" extra:"+extraField;
    }
}