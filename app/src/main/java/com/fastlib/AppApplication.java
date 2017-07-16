package com.fastlib;

import android.app.Application;

import com.fastlib.app.EventObserver;

/**
 * Created by sgfb on 17/7/13.
 */

public class AppApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        EventObserver.build(this);
    }
}
