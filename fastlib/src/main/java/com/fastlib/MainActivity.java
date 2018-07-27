package com.fastlib;

import android.os.Environment;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.app.task.NetAction;
import com.fastlib.app.task.Task;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

/**
 * Created by sgfb on 2018/7/25.
 */
@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{

    @Override
    public void alreadyPrepared() {

    }

    @Bind(R.id.bt)
    private void loadDex(){

    }
}
