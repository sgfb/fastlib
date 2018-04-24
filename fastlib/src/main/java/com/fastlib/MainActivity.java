package com.fastlib;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.processor.Net;

/**
 * Created by sgfb on 18/4/4.
 */
@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{

    @Override
    protected void alreadyPrepared(){

    }

    @Bind(R.id.bt)
    private void commit(){

    }
}