package com.fastlib.net;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * httpConnection封装<br/>
 * 这个类不具体处理网络事务，只分发任务和数据统计，调整网络配置，注重任务调配和任务处理结果统计<br/>
 * 网络在类被实现的时候开始工作（调配任务），在断网或者需要保存请求缓存的时候可以正确的保存请求等待下次使用<br/>
 * 网络任务请求具有关联性，默认是independ（无关联）<br/>
 * 同类型的网络请求必须等之前的同类型网络请求被移除队列后才能开始任务
 */
public class NetQueue {
    public static int TYPE_INDEPEND=0;

    private static PriorityBlockingQueue<Request> mWaitingQueue;
    private static ArrayList<Request> mReadyQueue;
    private static List<Request> mRunningQueue;
    private static NetQueue mOwer;
    private static Config mConfig;
    private static int Tx,Rx;
    private Map<Integer,Boolean> mBlockMap;
    private DataFactory mFactory;
    private String mRootAddress;
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
                        mBlockMap.put(processor1.getRequest().getType(),false);
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
                                mBlockMap.put(processor1.getRequest().getType(),false);
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
        mConfig.useStatus=true;
        mConfig.isTrackTraffic=true;
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
        netRequest(0, r);
    }

    public void netRequest(int type,Request r){
        if(mFactory!=null&&r.isUseFactory()){
            Map<String,String> map=r.getParams();
            if(map==null){
                map=new HashMap<>();
                r.setParams(map);
            }
            map.putAll(mFactory.extraData());
        }
        if(!TextUtils.isEmpty(mRootAddress)&&!r.isHadRootAddress()) {
            r.setUrl(mRootAddress + r.getUrl());
            r.setHadRootAddress(true);
        }
        mWaitingQueue.add(r);
    }

    public DataFactory getFactory(){
        return mFactory;
    }

    public void setFactory(DataFactory factory){
        mFactory=factory;
    }

    public void setConfig(@NonNull Config config){
        mConfig=config;
    }

    public Config getConfig(){
        return (Config)mConfig.clone();
    }

    public String getRootAddress() {
        return mRootAddress;
    }

    public void setRootAddress(String rootAddress) {
        mRootAddress = rootAddress;
    }

    public static class Config implements Cloneable{
        private boolean isTrackTraffic;
        private boolean useStatus;
        private int maxTask;

        public void setTrackTraffic(boolean track){
            isTrackTraffic=track;
        }

        public void setMaxTask(int max){
            maxTask=max;
        }

        public void setUseStatus(boolean use){
            useStatus=use;
        }

        public boolean isUseStatus(){
            return useStatus;
        }

        public boolean isTrackTraffic(){
            return isTrackTraffic;
        }

        public int getMaxTask(){
            return maxTask;
        }

        @Override
        public Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }
    }

    public interface DataFactory{
        Map<String,String> extraData();
    }
}