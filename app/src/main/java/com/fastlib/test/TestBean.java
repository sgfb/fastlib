package com.fastlib.test;

import com.fastlib.annotation.DatabaseInject;
import com.fastlib.bean.Entity;

/**
 * Created by sgfb on 16/2/11.
 */
@DatabaseInject(remoteUri = "http://192.168.26.107:8080/FastProject/Login")
public class TestBean{
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data){
        this.data = data;
    }
}
