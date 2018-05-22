package com.fastlib;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.fastlib.annotation.ContentView;

/**
 * Created by Administrator on 2018/5/18.
 */
@ContentView(R.layout.act_main)
public class LauncherActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LauncherBinding binding=DataBindingUtil.setContentView(this,R.layout.act_main);
        Bean bean=new Bean();
        bean.name="sgfb";
        binding.setBean(bean);
    }
}
