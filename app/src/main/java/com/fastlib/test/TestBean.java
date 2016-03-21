package com.fastlib.test;

import com.fastlib.annotation.DatabaseInject;
import com.fastlib.bean.Entity;

/**
 * Created by sgfb on 16/2/11.
 */
@DatabaseInject(remoteUri = "http://192.168.1.112:8080/FastProject/Login")
public class TestBean{
    @DatabaseInject(keyPrimary = true,autoincrement = true)
    private int id;
    private String data;
    private int data2;

    public int getData2() {
        return data2;
    }

    public void setData2(int data2) {
        this.data2 = data2;
    }

    public String getData() {
        return data;
    }

    public void setData(String data){
        this.data = data;
    }
}
