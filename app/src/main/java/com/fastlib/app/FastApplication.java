package com.fastlib.app;

import android.app.Application;
import android.os.Environment;

import com.fastlib.bean.NetFlow;
import com.fastlib.bean.UploadExceptionBean;
import com.fastlib.db.FastDatabase;
import com.fastlib.db.SaveUtil;
import com.fastlib.net.NetQueue;
import com.fastlib.test.UploadUncaughtException;
import com.fastlib.utils.TimeUtil;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * 全局环境配置
 */
public class FastApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        EventObserver.build(this);
//        new UploadUncaughtException(this,Thread.currentThread());
//        uploadLastException();
    }

    private void uploadLastException(){
        UploadExceptionBean bean= null;
        try {
            bean = (UploadExceptionBean) SaveUtil.getFromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"track.txt"));
            if(bean!=null) {
                UploadUncaughtException.uploadException(bean);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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
            FastDatabase.getDefaultInstance(this).saveOrUpdate(netFlow);
    }
}