package com.fastlib.app;

import android.app.Application;

import com.fastlib.db.FastDatabase;
import com.fastlib.net.NetQueue;

/**
 * 全局环境配置
 */
public class FastApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FastDatabase.build(this);
        EventObserver.build(this);
        NetQueue.getInstance();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        EventObserver.getInstance().clear();
    }
}
