package com.fastlib.app;

import android.app.Application;
import android.support.annotation.CallSuper;

import com.fastlib.bean.NetFlow;
import com.fastlib.db.FastDatabase;
import com.fastlib.net.NetQueue;
import com.fastlib.utils.TimeUtil;

import java.util.Date;

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

    /**
     * 保存一下流量使用情况，如果未使用不保存
     */
    private void saveNetFlow(){
        NetFlow netFlow =new NetFlow();
        netFlow.requestCount=NetQueue.getInstance().mRequestCount;
        netFlow.receiveByte=NetQueue.getInstance().Rx;
        netFlow.takeByte=NetQueue.getInstance().Tx;
        netFlow.time= TimeUtil.dateToString(new Date(System.currentTimeMillis()));
        if(netFlow.requestCount>0)
            FastDatabase.getDefaultInstance().saveOrUpdate(netFlow);
    }

    public static FastApplication getInstance(){
        return mApp;
    }
}
