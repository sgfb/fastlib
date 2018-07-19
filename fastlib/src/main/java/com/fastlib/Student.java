package com.fastlib;

import com.fastlib.annotation.Database;

/**
 * Created by sgfb on 18/7/19.
 */
public class Student{
    @Database(keyPrimary = true,autoincrement = true)
    public int id;
    public String a;

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", a='" + a + '\'' +
                '}';
    }
}
