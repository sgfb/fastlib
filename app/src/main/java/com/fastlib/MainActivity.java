package com.fastlib;


import android.os.Bundle;
import android.view.View;

import com.fastlib.annotation.Bind;
import com.fastlib.app.FastActivity;

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
    public void save(View v){

    }

    @Bind(R.id.bt2)
    public void show(View v){

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        getApplication().onTerminate();
    }
}