package com.fastlib;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.aspect.ThreadOn;
import com.fastlib.aspect.exception.ExceptionHandler;
import com.fastlib.net2.utils.RequestAgentFactory;

/**
 * Created by sgfb on 2020\03\01.
 */
@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity implements ExceptionHandler {

    @Override
    public void alreadyPrepared() {

    }

    @ThreadOn(ThreadOn.ThreadType.WORK)
    @Bind(R.id.bt)
    private void bt(){

    }

    @Bind(R.id.bt2)
    private void bt2(){

    }

    @Override
    public void onException(Exception e) {
        e.printStackTrace();
    }
}
