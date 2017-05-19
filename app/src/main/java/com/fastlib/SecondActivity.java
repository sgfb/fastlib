package com.fastlib;

import android.widget.TextView;

import com.fastlib.app.FastActivity;
import com.fastlib.app.TaskAction;
import com.fastlib.app.TaskChainHead;

/**
 * Created by sgfb on 17/5/19.
 */
public class SecondActivity extends FastActivity{

    @Override
    protected void alreadyPrepared(){
        throw new NullPointerException("unsupported this action");
    }
}