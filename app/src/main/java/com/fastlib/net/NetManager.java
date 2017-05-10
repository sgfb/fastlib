package com.fastlib.net;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.fastlib.app.GlobalConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by sgfb on 16/9/1.
 * 这个类不具体处理网络事务，只分发任务和数据统计，调整网络配置，注重任务调配和任务处理结果统计<br/>
 * 网络在类被实现的时候开始工作（调配任务），在断网或者需要保存请求缓存的时候可以正确的保存请求等待下次使用
 */
public class NetManager{
    private static NetManager mOwer;
    public int mRequestCount=0;
    public long Tx,Rx;
    public static ThreadPoolExecutor sRequestPool =(ThreadPoolExecutor) Executors.newFixedThreadPool(10); //公共网络请求池
    private DataFactory mFactory;
    private Config mConfig;
    private String mRootAddress;
    private Listener mGlobalListener; //一个全局的事件回调监听，所有网络回调给具体回调之前做一次回调

    private NetManager(){
        mConfig=new Config();
    }

    public static synchronized NetManager getInstance(){
        if(mOwer==null)
            mOwer = new NetManager();
        return mOwer;
    }

    /**
     * 网络任务入队列
     * @param request
     */
    public void netRequest(Request request){
        if(mFactory!=null&&request.isUseFactory()){
            List<Pair<String,String>> params=request.getParams();
            if(params==null){
                params=new ArrayList<>();
                request.setParams(params);
            }
            if(mFactory.extraData()!=null)
                params.addAll(mFactory.extraData());
        }
        if(!TextUtils.isEmpty(mRootAddress)&&!request.isHadRootAddress()){
            request.setUrl(mRootAddress + request.getUrl());
            request.setHadRootAddress(true);
        }
        enqueue(request);
    }

    /**
     * 网络请求内部入队列处理
     * @param request
     */
    private void enqueue(Request request){
        ThreadPoolExecutor pool=request.getExecutor();
        NetProcessor processor=new NetProcessor(request,new NetProcessor.OnCompleteListener(){
            @Override
            public void onComplete(NetProcessor processor1){
                mRequestCount++;
                Tx+=processor1.getTx();
                Rx+=processor1.getRx();
                if(GlobalConfig.SHOW_LOG)
                    System.out.println(processor1);
            }
        },new Handler(Looper.getMainLooper()));
        if(pool!=null) pool.execute(processor);
        else sRequestPool.execute(processor);
    }

    public void close(){
        sRequestPool.shutdownNow();
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

    public Listener getGlobalListener() {
        return mGlobalListener;
    }

    public void setGlobalListener(Listener globalListener) {
        mGlobalListener = globalListener;
    }

    public static class Config implements Cloneable{
        private boolean isTrackTraffic;
        private List<String> mTrustHost; //信任站点，当前仅用于过滤保存时间

        public void setTrackTraffic(boolean track){
            isTrackTraffic=track;
        }

        public boolean isTrackTraffic(){
            return isTrackTraffic;
        }

        public List<String> getTrustHost() {
            return mTrustHost;
        }

        public void setTrustHost(List<String> trustHost) {
            mTrustHost = trustHost;
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
        List<Pair<String,String>> extraData();
    }
}
