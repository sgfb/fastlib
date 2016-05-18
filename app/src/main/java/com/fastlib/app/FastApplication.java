package com.fastlib.app;

import android.app.Application;

import com.fastlib.db.FastDatabase;
import com.fastlib.net.NetQueue;

/**
 * 全局环境配置
 */
public class FastApplication extends Application{
    private static FastApplication mApp;
    private String mRootAddress;

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

    public String getRootAddress() {
        return mRootAddress;
    }

    public void setRootAddress(String rootAddress) {
        mRootAddress = rootAddress;
    }
}
