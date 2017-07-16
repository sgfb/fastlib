package com.fastlib;

import com.fastlib.annotation.ContentView;
import com.fastlib.annotation.Event;
import com.fastlib.app.FastFragment;

/**
 * Created by sgfb on 17/7/13.
 */
@ContentView(R.layout.act_detail)
public class TestFragment extends FastFragment{
    byte[] data=new byte[2000000];

    @Override
    protected void alreadyPrepared() {

    }

    @Event
    private void justTest(long l){
        System.out.println("long:"+l);
    }
}
