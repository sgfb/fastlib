package com.hhkj.mylibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.fastlib.app.module.Module;

/**
 * Created by sgfb on 2018/7/24.
 */
@Module("cc")
public class LibActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mylib_act_main);
    }
}