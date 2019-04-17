package com.fastlib;

import android.widget.EditText;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.widget.AutoFitGridView;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity {
    @Bind(R.id.autoFitGrid)
    AutoFitGridView mAutoFitGrid;
    @Bind(R.id.text)
    EditText mText;

    @Override
    public void alreadyPrepared() {
    }

    @Bind(R.id.bt)
    private void startServer() {

    }

    @Bind(R.id.bt2)
    private void startClient() {

    }
}