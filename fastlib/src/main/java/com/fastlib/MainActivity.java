package com.fastlib;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.db.FastDatabase;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity {

    @Bind(R.id.bt)
    private void startServer() {

    }

    @Bind(R.id.bt2)
    private void bt2(){

    }

    @Override
    public void alreadyPrepared() {

    }
}