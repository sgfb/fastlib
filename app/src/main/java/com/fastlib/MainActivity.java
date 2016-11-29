package com.fastlib;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.fastlib.annotation.Bind;
import com.fastlib.app.FastActivity;
import com.fastlib.db.SaveUtil;

import java.io.File;

/**
 * Created by sgfb on 16/5/10.
 */
public class MainActivity extends FastActivity{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Bind(R.id.bt)
    public void commit1(View v){

    }

    @Bind(R.id.bt2)
    public void commit2(View v){

    }
}