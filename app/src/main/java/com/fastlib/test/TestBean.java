package com.fastlib.test;

import com.fastlib.annotation.DatabaseInject;

/**
 * Created by sgfb on 16/2/11.
 */
@DatabaseInject(remoteUri = "http://192.168.0.102:8080/FastProject/Login")
public class TestBean {

    @DatabaseInject(keyPrimary = true,autoincrement = true)
    private int id;

    private String data;
    private int extra;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
