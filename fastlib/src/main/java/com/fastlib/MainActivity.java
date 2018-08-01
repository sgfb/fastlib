package com.fastlib;

import android.content.Intent;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.hhkj.mylibrary.LibActivity;

/**
 * Created by sgfb on 2018/7/25.
 */
@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{

    @Override
    public void alreadyPrepared() {

    }

    @Bind(R.id.bt)
    private void bt(){
        Intent intent=new Intent(this, LibActivity.class);
        startActivity(intent);
    }

    @Bind(R.id.bt2)
    private void bt2(){
    }
}
