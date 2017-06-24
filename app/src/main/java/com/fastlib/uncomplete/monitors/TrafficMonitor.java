package com.fastlib.uncomplete.monitors;

import android.net.TrafficStats;

/**
 * Created by sgfb on 17/1/30.
 * 指定uid监控流量
 */
public class TrafficMonitor{
    private boolean isRunning=true;
    private int mUid;
    private long mInterval=1000;
    private long mLastRx;
    private long mLastTx;
    private NetCallback mCallback;

    public TrafficMonitor(int uid, long interval,boolean startNow,NetCallback callback){
        mUid = uid;
        mInterval = interval;
        mLastRx= TrafficStats.getUidRxBytes(uid);
        mLastTx=TrafficStats.getUidTxBytes(uid);
        mCallback=callback;
        if(startNow)
            start();
    }

    public void start(){
        isRunning=true;
        try{
            while(isRunning){
                Thread.sleep(mInterval);
                long currRx=TrafficStats.getUidRxBytes(mUid);
                long currTx=TrafficStats.getUidTxBytes(mUid);
                if(mCallback!=null)
                    mCallback.onCallback(currRx-mLastRx,currTx-mLastTx);
                mLastRx=currRx;
                mLastTx=currTx;
            }
        }catch (InterruptedException e){

        }
    }

    public void stop(){
        isRunning=false;
    }

    public interface NetCallback{
        void onCallback(long rx,long tx);
    }
}