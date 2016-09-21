package com.fastlib.app;

import android.app.Application;

import com.fastlib.db.FastDatabase;

/**
 * 全局环境配置
 */
public class FastApplication extends Application{
    public static final String NAME_SHAREPREFERENCES="fastlib";
    private static FastApplication mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp=this;
        FastDatabase.build(this);
        EventObserver.build(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        EventObserver.getInstance().clear();
    }

    public static FastApplication getInstance(){
        return mApp;
    }
}
