package com.fastlib;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.db.FastDatabase;
import com.fastlib.net.NetManager;
import com.fastlib.net.Request;
import com.fastlib.net.listener.SimpleListener;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity {
    CommonInterface_G mCommonModel=new CommonInterface_G(getModuleLife());

    @Bind(R.id.bt)
    private void startServer(){
        mCommonModel.getBaidu();
    }

    @Bind(R.id.bt2)
    private void bt2(){

    }

    @Override
    public void alreadyPrepared() {
        NetManager.getInstance().setRootAddress("http://111.231.85.35");
    }
}