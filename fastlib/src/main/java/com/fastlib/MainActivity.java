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
    MainPresenter mPresenter;

    @Override
    protected void alreadyPrepared(){
        mPresenter=new MainPresenter();
    }

    @Bind(R.id.bt)
    private void commit(){
        mPresenter.login("1234","123");
    }
}