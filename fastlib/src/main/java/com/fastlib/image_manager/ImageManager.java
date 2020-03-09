package com.fastlib.image_manager;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import android.util.Log;

import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.image_manager.state.TypeCheckState;
import com.fastlib.image_manager.bean.ImageConfig;
import com.fastlib.image_manager.request.CallbackParcel;
import com.fastlib.image_manager.request.ImageRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 管理图像请求任务调度
 * 所有的请求都必须有回调，无论成功与否，因为调度依赖请求回调
 * 有请求任务入队列前判断是否有相同url请求存在，如果没有进入正常队列否则移入阻塞队列
 */
public class ImageManager{
    public static final String TAG=ImageManager.class.getSimpleName();
    private int mMaxRunning= ThreadPoolManager.sSlowPool.getMaximumPoolSize()/2+1;
    private CallbackParcel mGlobalCallback;
    private List<ImageRequest> mRunningList=new ArrayList<>();                      //运行中队列
    private List<ImageRequest> mWaitingList =new ArrayList<>();                     //等待中队列
    private Map<ImageRequest,List<ImageRequest>> mPendingList=new HashMap<>();      //阻塞的请求
    private ImageConfig mConfig=new ImageConfig();
    private static ImageManager mOwner;

    List<ImageRequest> getNormalList(){
        return mWaitingList;
    }

    Map<ImageRequest,List<ImageRequest>> getPendingList(){
        return mPendingList;
    }

    public static synchronized ImageManager getInstance(){
        if(mOwner==null) mOwner=new ImageManager();
        return mOwner;
    }

    private ImageManager(){
        mGlobalCallback=new CallbackParcel() {
            @Override
            public void prepareLoad(ImageRequest request) {

            }

            @Override
            public void success(ImageRequest request,Bitmap Bitmap) {
                completeRequest(request);
            }

            @Override
            public void failure(ImageRequest request,Exception e) {
                completeRequest(request);
            }
        };
    }

    public void addRequest(final ImageRequest request){
        if(request==null||request.getSource()==null) return;

        boolean runNow=false;
        int queueType;
        if(request.getCallbackParcel()!=null) request.getCallbackParcel().prepareLoad(request);
        if(mRunningList.contains(request)||mWaitingList.contains(request)){
            queueType=2;
            List<ImageRequest> list=mPendingList.get(request);
            if(list==null){
                list=new ArrayList<>();
                mPendingList.put(request,list);
            }
            list.add(request);
        }
        else{
            queueType=1;
            if(mRunningList.size()<mMaxRunning){
                mRunningList.add(request);
                runNow=true;
            }
            else mWaitingList.add(request);
        }
        Log.d(TAG,String.format(Locale.getDefault(),"---%s（%s）进入队列----->%d",
                request.getSimpleName(),queueType==1?"运行":"阻塞",queueType==1?mRunningList.size():getPendingListSize()));
        if(runNow) ThreadPoolManager.sSlowPool.execute(new TypeCheckState(request));
    }

    private synchronized void completeRequest(ImageRequest request){
        mRunningList.remove(request);
        Log.d(TAG,String.format(Locale.getDefault(),"<---%s请求结束-----%d",request.getSimpleName(),mRunningList.size()));
        request.clean();
        List<ImageRequest> list=mPendingList.get(request);
        if(list!=null&&!list.isEmpty()) {
            mWaitingList.add(list.remove(0));
            Log.d(TAG,String.format(Locale.getDefault(),"%s-----阻塞转等待--->%s",getPendingListSize(),mWaitingList.size()));
        }
        else mPendingList.remove(request);
        if(mRunningList.size()<mMaxRunning&&!mWaitingList.isEmpty()) {
            ImageRequest r=mWaitingList.remove(mWaitingList.size()-1);
            mRunningList.add(r);
            Log.d(TAG,String.format(Locale.getDefault(),"%s-----等待转运行--->%s",mWaitingList.size(),mRunningList.size()));
            ThreadPoolManager.sSlowPool.execute(new TypeCheckState(r));
        }
    }

    public CallbackParcel getCallbackParcel(){
        return mGlobalCallback;
    }

    public ImageConfig getConfig() {
        return mConfig.clone();
    }

    public void setConfig(@NonNull ImageConfig config){
        mConfig=config;
    }

    private int getPendingListSize(){
        int count=0;
        for(Map.Entry<ImageRequest,List<ImageRequest>> entry:mPendingList.entrySet())
            count=entry.getValue().size();
        return count;
    }
}