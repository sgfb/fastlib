package com.fastlib;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.fastlib.adapter.FastAdapter;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.LocalData;
import com.fastlib.app.FastActivity;
import com.fastlib.base.OldViewHolder;
import com.fastlib.db.And;
import com.fastlib.db.FastDatabase;
import com.fastlib.db.FilterCondition;
import com.fastlib.test.SlideDeleteView2;
import com.fastlib.test.ZipUtils;
import com.fastlib.utils.N;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
    private void openSecondActivity(View v){

    }

    @Bind(R.id.bt2)
    private void commit2(View v){

    }
}