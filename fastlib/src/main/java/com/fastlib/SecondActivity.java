package com.fastlib;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;

/**
 * Created by sgfb on 2018/7/24.
 */
@Module("bb")
@ContentView(R.layout.act_main)
public class SecondActivity extends FastActivity{

    @Override
    protected void alreadyPrepared() {

    }

    @Bind(R.id.bt)
    private void bt(){
        ModuleLauncher.getInstance().start(this,"cc");
    }
}
