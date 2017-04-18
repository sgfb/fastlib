package com.fastlib.app;

import android.content.Context;

import com.fastlib.bean.NetFlow;
import com.fastlib.db.And;
import com.fastlib.db.FastDatabase;
import com.fastlib.db.FilterCommand;
import com.fastlib.db.FilterCondition;
import com.fastlib.net.NetQueue;
import com.fastlib.utils.TimeUtil;

import java.io.File;
import java.util.Date;

/**
 * 全局环境配置
 */
public class Plus{

    /**
     * 保存一下流量使用情况，如果未使用不保存
     */
    public static void saveNetFlow(Context context){
        NetFlow netFlow =new NetFlow();
        netFlow.requestCount=NetQueue.getInstance().mRequestCount;
        netFlow.receiveByte=NetQueue.getInstance().Rx;
        netFlow.takeByte=NetQueue.getInstance().Tx;
        netFlow.time= TimeUtil.dateToString(new Date(System.currentTimeMillis()),"yyyy-MM-dd");

        NetFlow existsHistory=FastDatabase.getDefaultInstance(context).addFilter(new And(FilterCondition.equal(netFlow.time))).getFirst(NetFlow.class);
        if(existsHistory!=null){
            netFlow.requestCount+=existsHistory.requestCount;
            netFlow.receiveByte+=existsHistory.receiveByte;
            netFlow.takeByte+=existsHistory.takeByte;
        }
        if(netFlow.requestCount>0)
            FastDatabase.getDefaultInstance(context).saveOrUpdate(netFlow);
    }
}