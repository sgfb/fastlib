package com.fastlib;

import android.os.Bundle;
import android.view.View;

import com.fastlib.annotation.Bind;
import com.fastlib.app.FastActivity;
import com.fastlib.db.And;
import com.fastlib.db.DatabaseListener;
import com.fastlib.db.FastDatabase;
import com.fastlib.db.FilterCommand;
import com.fastlib.db.FilterCondition;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by sgfb on 16/12/29.
 */
public class MainActivity extends FastActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
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