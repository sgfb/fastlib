package com.fastlib;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.fastlib.annotation.Bind;
import com.fastlib.app.FastActivity;
import com.fastlib.bean.PermissionRequest;

import java.util.HashMap;
import java.util.Map;

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