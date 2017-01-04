package com.fastlib;

import com.fastlib.annotation.Database;

/**
 * Created by sgfb on 17/1/4.
 */
public class Bean{
    @Database(keyPrimary = true,autoincrement = true)
    public int id;
    public String name;
    public String sex;

    @Override
    public String toString() {
        return "id:"+id+" name:"+name+" sex:"+sex;
    }
}