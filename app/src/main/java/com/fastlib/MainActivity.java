package com.fastlib;

import android.os.Environment;
import android.os.StatFs;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;

import java.io.File;
import java.io.FileOutputStream;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{


    @Override
    protected void alreadyPrepared(){

    }

    @Bind(R.id.bt)
    private void commit(){

    }

    @Bind(R.id.bt2)
    private void commit2(){

    }
}