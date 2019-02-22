package com.fastlib.url_image;

import android.graphics.Bitmap;
import android.util.Log;

import com.fastlib.net.NetManager;
import com.fastlib.url_image.bean.ImageConfig;
import com.fastlib.url_image.request.CallbackParcel;
import com.fastlib.url_image.request.ImageRequest;

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
    private int mMaxRunning=NetManager.sRequestPool.getMaximumPoolSize()/2+1;
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
                NetManager.sRequestPool.execute(new TypeCheckState(request));
            }
            else mWaitingList.add(request);
        }
        Log.d(TAG,String.format(Locale.getDefault(),"---%s（%s）进入队列----->%s",
                request.getSimpleName(),queueType==1?"运行队列":"阻塞队列",queueType==1?mRunningList.size():mWaitingList.size()));
    }

    private synchronized void completeRequest(ImageRequest request){
        Log.d(TAG,String.format(Locale.getDefault(),"<---%s请求结束-----",request.getSimpleName()));
        request.clean();
        List<ImageRequest> list=mPendingList.get(request);
        if(list!=null&&!list.isEmpty())
            mWaitingList.add(list.remove(0));
        else mPendingList.remove(request);
        mRunningList.remove(request);
        if(mRunningList.size()<mMaxRunning&&!mWaitingList.isEmpty()) {
            ImageRequest r=mWaitingList.remove(mWaitingList.size()-1);
            mRunningList.add(r);
            NetManager.sRequestPool.execute(new TypeCheckState(r));
        }
    }

    public CallbackParcel getCallbackParcel(){
        return mGlobalCallback;
    }

    public ImageConfig getConfig() {
        return mConfig.clone();
    }
}