package com.fastlib;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by sgfb on 2020\03\01.
 */
public class SecondActivity extends AppCompatActivity {
    public static final String ARG_INT_NUM="num";
    public static final String ARG_STR_NAME="name";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int num=getIntent().getIntExtra(ARG_INT_NUM,-1);
        String name=getIntent().getStringExtra(ARG_STR_NAME);
        System.out.println("num:"+num+" name:"+name);
    }
}
