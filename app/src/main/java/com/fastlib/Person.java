package com.fastlib;

import com.fastlib.annotation.Database;

/**
 * Created by sgfb on 16/9/22.
 */
public class Person{
    @Database(keyPrimary = true,autoincrement = true)
    public int id;
    public String name;

    @Override
    public String toString(){
        return "id:"+id+" name:"+name;
    }
}