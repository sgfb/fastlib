package com.fastlib.net;

import android.os.Handler;
import android.os.Looper;

import com.fastlib.test.TestGlobal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * httpConnection封装
 * 这个类不具体处理网络事务，只分发任务和数据统计，调整网络配置，注重任务调配和任务处理结果统计
 * 网络在类被实现的时候开始工作（调配任务），在断网或者需要保存请求缓存的时候可以正确的保存请求等待下次使用
 * 网络任务请求具有关联性，默认是independ（无关联）
 * 同类型的网络请求必须等之前的同类型网络请求被移除队列后才能开始任务
 */
public class NetQueue {
    public static int TYPE_INDEPEND=0;

    private static PriorityBlockingQueue<Request> mWaitingQueue;
    private static ArrayList<Request> mReadyQueue;
    private static List<Request> mRunningQueue;
    private Map<Integer,Boolean> mBlockMap;
    private static NetQueue mOwer;
    private static Config mConfig;
    private static int Tx,Rx;
    private volatile int mProcessing;
    private Runnable mMainProcessor =new Runnable() {
        @Override
        public void run() {
            while(true){
                Request r= null;
                try {
                    r = mWaitingQueue.take();
                    if(r.getType()!=TYPE_INDEPEND){
                        Boolean isBlock=mBlockMap.get(r.getType());
                        if(isBlock==null||!isBlock){
                            mBlockMap.put(r.getType(),true);
                        }
                        else{
                            mReadyQueue.add(r);
                            continue;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                NetProcessor processor=new NetProcessor(r, new NetProcessor.OnCompleteListener() {
                    @Override
                    public void onComplete(NetProcessor processor1){
                        System.out.println(processor1);
                        mProcessing--;
                        mBlockMap.put(processor1.getReqeust().getType(),false);
                        if(mProcessing<=mConfig.maxTask)
                            new Thread(callbackRunner).start();
                    }
                },new Handler(Looper.getMainLooper()));
                new Thread(processor).start();
                mProcessing++;
                if(mProcessing>=mConfig.maxTask)
                    break;
            }
        }
    };
    private Runnable callbackRunner=new Runnable() {
        @Override
        public void run(){
            if(mReadyQueue.size()>0){
                Iterator<Request> iter=mReadyQueue.iterator();

                while(iter.hasNext()){
                    Request r=iter.next();
                    Boolean isBlock=mBlockMap.get(r.getType());
                    if(isBlock==null||!isBlock){
                        NetProcessor processor=new NetProcessor(r, new NetProcessor.OnCompleteListener() {
                            @Override
                            public void onComplete(NetProcessor processor1) {
                                System.out.println(processor1);
                                mProcessing--;
                                mBlockMap.put(processor1.getReqeust().getType(),false);
                                if(mProcessing<=mConfig.maxTask) {
                                    new Thread(callbackRunner).start();
                                }
                            }
                        },new Handler(Looper.getMainLooper()));
                        new Thread(processor).start();
                        break;
                    }
                }
            }
            else{
                new Thread(mMainProcessor).start();
            }
        }
    };

    private NetQueue(){
        mConfig=new Config();
        mWaitingQueue=new PriorityBlockingQueue<>();
        mConfig.maxTask=5;
        mRunningQueue=new ArrayList<>();
        mBlockMap=new HashMap<>();
        mReadyQueue=new ArrayList<>();
        new Thread(mMainProcessor).start();
    }

    public static synchronized NetQueue getInstance(){
        if(mOwer==null)
            mOwer=new NetQueue();
        return mOwer;
    }

    public void netRequest(final Request r){
        netRequest(0,r);
    }

    public void netRequest(int type,Request r){
        r.setUrl(TestGlobal.getInstance().getRootAddress() + r.getUrl());
        mWaitingQueue.add(r);
    }

    public void setConfig(Config config){
        if(config==null)
            throw new RuntimeException("不可以将网络请求设置为null");
        mConfig=config;
    }

    public static class Config{
        private boolean isTrackTraffic;
        private int maxTask;

        public void setTrackTraffic(boolean track){
            isTrackTraffic=track;
        }

        public void setMaxTask(int max){
            maxTask=max;
        }
    }
}
