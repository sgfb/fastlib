package com.fastlib;

import android.content.Intent;
import android.widget.TextView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.annotation.LocalData;
import com.fastlib.app.FastActivity;

/**
 * Created by sgfb on 18/3/6.
 */
@ContentView(R.layout.act_main)
public class SecondActivity extends FastActivity{

    @Override
    protected void alreadyPrepared() {

    }

    @Bind(R.id.bt)
    private void commit()
    {
    }
}
