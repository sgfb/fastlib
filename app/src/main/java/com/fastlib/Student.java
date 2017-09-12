package com.fastlib;

import com.fastlib.annotation.Database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 17/9/11.
 */
public class Student{
    @Database(keyPrimary = true,autoincrement = true)
    public int id;
    public int age;
    public int score;
    public String name;
    public List<Bean<String>> list=new ArrayList<>();
}