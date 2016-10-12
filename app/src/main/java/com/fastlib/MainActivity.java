package com.fastlib;


import android.os.Bundle;
import android.view.View;

import com.fastlib.adapter.BindingJsonAdapter;
import com.fastlib.base.JsonActivity;
import com.fastlib.net.Request;

/**
 * Created by sgfb on 16/5/10.
 */
public class MainActivity extends JsonActivity{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public BindingJsonAdapter generateAdapter(){
        return null;
    }

    @Override
    public Request generateContentRequest(){
        return null;
    }

    @Override
    public void inflaterContent(View contentView){
        System.out.println("修改jsonAdapter支持多接口多类型，同步修改JsonActivity");
    }
}