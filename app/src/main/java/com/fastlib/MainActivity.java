package com.fastlib;

import android.view.View;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;

import com.fastlib.app.FastActivity;

/**
 * Created by sgfb on 16/12/29.
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends FastActivity{

    @Bind(R.id.bt)
    private void commit(View v){

    }

    @Bind(R.id.bt2)
    private void commit2(View v){

    }

    @Override
    protected void alreadyPrepared(){

    }
}