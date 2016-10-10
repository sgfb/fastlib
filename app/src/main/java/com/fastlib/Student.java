package com.fastlib;

import com.fastlib.annotation.Database;

/**
 * Created by sgfb on 16/10/8.
 */
public class Student{
    @Database(keyPrimary = true,autoincrement = true)
    public int id;
    public String name;
    public String address;

    public Student(){}

    public Student(int id,String name,String address){
        this.id=id;
        this.name=name;
        this.address=address;
    }
}
