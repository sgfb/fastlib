package com.fastlib.uncomplete.monitors;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by sgfb on 17/1/28.
 * cpu使用率监控.这个监控器不能运行在主线程中
 */
public class CpuMonitor{
    private boolean isRunning=true;
    private long mInterval=1000; //监控频率，默认每秒收集一次
    private int mCpuCount=1; //cpu数量
    private CpuRateCallback mCallback;
    private CpuTimer[] mLastCpuTimer;

    public CpuMonitor(long interval,CpuRateCallback callback,boolean startNow){
        mCpuCount=Runtime.getRuntime().availableProcessors();
        mInterval=interval;
        mCallback=callback;
        mLastCpuTimer=new CpuTimer[mCpuCount];
        for(int i=0;i<mCpuCount;i++)
            mLastCpuTimer[i]=new CpuTimer();
        if(startNow)
            start();
    }

    public void start(){
        isRunning=true;
        try {
            while(isRunning){
                Thread.sleep(mInterval);
                try {
                    if(mCallback==null) //没有监听则不检测
                        return;
                    SingleCpuInfo[] infos=new SingleCpuInfo[mCpuCount];
                    BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream("/proc/stat")));
                    reader.readLine(); //跳过第一行(cpu的使用率总计)
                    for(int i=0;i<mCpuCount;i++){
                        String[] columns=reader.readLine().split(" "); //分列
                        int consumeCount=0;
                        int idleTime=Integer.parseInt(columns[4]);
                        for(int j=1;j<columns.length;j++)
                            consumeCount+=Float.parseFloat(columns[j]);
                        int rCount=consumeCount- mLastCpuTimer[i].count;
                        int rIdle=idleTime- mLastCpuTimer[i].idle;
                        int usageTime=rCount-rIdle;
                        SingleCpuInfo info=new SingleCpuInfo();
                        if(rCount==0)
                            rCount=1;
                        info.cpuName=columns[0];
                        info.rate=usageTime*100/rCount;
                        infos[i]=info;
                        mLastCpuTimer[i].idle=idleTime;
                        mLastCpuTimer[i].count=consumeCount;
                        if("intr".equals(info.cpuName)){
                            info.cpuName="休眠CPU";
                            info.rate=0;
                        }
                    }
                    reader.close();
                    mCallback.onCallback(infos);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        isRunning=false;
    }

    public long getInterval(){
        return mInterval;
    }

    public void setInterval(long interval) {
        mInterval = interval;
    }

    public class CpuTimer{
        public int idle; //空闲时长
        public int count; //总时长
    }

    public class SingleCpuInfo{
        public String cpuName;
        public float rate; //当前速率
    }

    public interface CpuRateCallback{
        void onCallback(SingleCpuInfo[] info);
    }
}