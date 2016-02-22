package com.fastlib.app;

import android.app.Application;

import com.fastlib.db.FastDatabase;
import com.fastlib.net.NetQueue;

/**
 * 全局环境配置
 */
public class FastApplication extends Application{
    private static FastApplication mApp;
    private AppGlobal mGlobal;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp=this;
        FastDatabase.build(this);
        EventObserver.build(this);
        NetQueue.getInstance();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        EventObserver.getInstance().clear();
    }

    public static FastApplication getInstance(){
        return mApp;
    }

    public void setGlobal(AppGlobal global){
        mGlobal=global;
    }

    public AppGlobal getGlobal(){
        return mGlobal;
    }
}
