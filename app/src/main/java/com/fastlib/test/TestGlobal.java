package com.fastlib.test;

import com.fastlib.app.AppGlobal;

/**
 * Created by sgfb on 16/2/12.
 */
public class TestGlobal extends AppGlobal{
    private static TestGlobal mOwer;

    private TestGlobal() {
        super();
    }

    public static synchronized TestGlobal getInstance(){
        if(mOwer==null)
            mOwer=new TestGlobal();
        return mOwer;
    }
}
