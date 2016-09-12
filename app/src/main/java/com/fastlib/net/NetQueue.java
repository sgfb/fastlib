package com.fastlib.net;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by sgfb on 16/9/1.
 * httpConnection封装<br/>
 * 这个类不具体处理网络事务，只分发任务和数据统计，调整网络配置，注重任务调配和任务处理结果统计<br/>
 * 网络在类被实现的时候开始工作（调配任务），在断网或者需要保存请求缓存的时候可以正确的保存请求等待下次使用
 */
public class NetQueue{
    private static NetQueue mOwer;
    private static int Tx,Rx;
    private ThreadPoolExecutor mRequestPool;
    private BlockingQueue<Runnable> mRequestQueue;
    private DataFactory mFactory;
    private Config mConfig;
    private String mRootAddress;

    private NetQueue(){
        mRequestQueue=new ArrayBlockingQueue<>(30);
        mRequestPool=new ThreadPoolExecutor(8,30,30, TimeUnit.SECONDS,mRequestQueue);
    }

    public static synchronized NetQueue getInstance(){
        if(mOwer==null)
            mOwer=new NetQueue();
        return mOwer;
    }

    /**
     * 网络任务入队列
     * @param request
     */
    public void netRequest(Request request){
        if(mFactory!=null&&request.isUseFactory()){
            Map<String,String> map=request.getParams();
            if(map==null){
                map=new HashMap<>();
                request.setParams(map);
            }
            map.putAll(mFactory.extraData());
        }
        if(!TextUtils.isEmpty(mRootAddress)&&!request.isHadRootAddress()) {
            request.setUrl(mRootAddress + request.getUrl());
            request.setHadRootAddress(true);
        }
        enqueue(request);
    }

    private void enqueue(Request request){
        NetProcessor processor=new NetProcessor(request,new NetProcessor.OnCompleteListener() {
            @Override
            public void onComplete(NetProcessor processor1){
                Tx+=processor1.getTx();
                Rx+=processor1.getRx();
                System.out.println(processor1);
            }
        },new Handler(Looper.getMainLooper()));
        mRequestPool.execute(processor);
    }

    public void close(){
        mRequestPool.shutdownNow();
        mOwer=null;
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
