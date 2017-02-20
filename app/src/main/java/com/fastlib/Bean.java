package com.fastlib;

import com.fastlib.annotation.Database;

import java.io.Serializable;

/**
 * Created by sgfb on 17/2/3.
 */

public class Bean implements Serializable{
    @Database(keyPrimary = true,autoincrement = true)
    public int id;
    public String name;
    public int age;

    public Bean(){

    }

    public Bean(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "id:"+id+" name:"+name+" age:"+age;
    }
}
