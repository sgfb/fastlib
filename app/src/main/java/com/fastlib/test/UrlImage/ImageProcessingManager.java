package com.fastlib.test.UrlImage;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.widget.ImageView;

import com.fastlib.bean.StringTable;
import com.fastlib.db.And;
import com.fastlib.db.Condition;
import com.fastlib.db.DatabaseNoDataResultCallback;
import com.fastlib.db.FastDatabase;
import com.fastlib.net.DefaultDownload;
import com.fastlib.net.NetManager;
import com.fastlib.net.Request;
import com.fastlib.net.SimpleListener;
import com.fastlib.test.UrlImage.processing_state.StateDownloadImageIfExpire;
import com.fastlib.test.UrlImage.processing_state.StateFirstLoadImageOnDisk;
import com.fastlib.utils.ScreenUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by sgfb on 18/1/4.
 * 持有正在处理和待处理的{@link BitmapRequest}
 * 监听对应Activity和Fragment的生命周期回调
 * 1.接到{@link BitmapRequest}时判断本地资源是否过期,根据缓存策略判断内存和硬盘缓存来决定使用是否下载,移入内存使用
 * 2.下载中监听到中断生命回调则暂停下载，监听到销毁生命回调移除任务
 * 3.下载和硬盘中图像读取到内存中线程调度
 */
public class ImageProcessingManager{
    private BitmapReferenceManager mBitmapReferenceManager;
    private List<BitmapRequest> mRequestList=new ArrayList<>();
    private BlockingQueue<UrlImageProcessing> mDiskLoaderQueue=new ArrayBlockingQueue<>(2);
    private BlockingQueue<UrlImageProcessing> mNetDownloaderQueue=new ArrayBlockingQueue<>(2);

    public ImageProcessingManager(BitmapReferenceManager bitmapReferenceManager){
        mBitmapReferenceManager=bitmapReferenceManager;
        //长期占用两根线程作为服务器中数据下载到本地和本地读取到内存中处理工作(后期改为工作线程调度)
        NetManager.sRequestPool.execute(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("ImageDiskLoader");
                try{
                    while(true){
                        UrlImageProcessing processing = mDiskLoaderQueue.take();
                        processing.handle(ImageProcessingManager.this);
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        });
        NetManager.sRequestPool.execute(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("ImageNetDownloader");
                try{
                    while(true){
                        UrlImageProcessing processing = mNetDownloaderQueue.take();
                        processing.handle(ImageProcessingManager.this);
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 图像处理状态转换
     * @param diskState 硬盘或网络队列标志
     * @param imageProcessing 处理器
     */
    public void imageProcessStateConvert(boolean diskState,UrlImageProcessing imageProcessing){
        try{
            if(diskState) mDiskLoaderQueue.put(imageProcessing);
            else mNetDownloaderQueue.put(imageProcessing);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void addBitmapRequest(Activity activity,BitmapRequest request,ImageView imageView){
        final UrlImageProcessing urlImageProcessing=generateFirstImageProcessing(request);
        activity.getApplication().registerActivityLifecycleCallbacks(new AdapterActivityLifecycleCallbacks(){
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                urlImageProcessing.onStart(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                urlImageProcessing.onPause(activity);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                urlImageProcessing.onDestroy(activity);
            }
        });
        internalAddBitmapRequest(activity,request,imageView);
    }

    public void addBitmapRequest(Fragment fragment,BitmapRequest request,ImageView imageView){
        final UrlImageProcessing urlImageProcessing=generateFirstImageProcessing(request);
        fragment.getChildFragmentManager()
                .beginTransaction()
                .add(null,"lifecycle")
                .commit();
        internalAddBitmapRequest(fragment.getActivity(),request,imageView);
    }

    private UrlImageProcessing generateFirstImageProcessing(BitmapRequest request){
        return BitmapRequest.getSaveFile(request).exists()?
                new StateFirstLoadImageOnDisk(request,null):
                new StateDownloadImageIfExpire(request,null);
    }

    /**
     * 增加图像请求.最初过滤，如果图片存在内存中直接取内存
     * 如果存在磁盘中调起{@link com.fastlib.test.UrlImage.processing_state.StateFirstLoadImageOnDisk}
     * 否则直接调起{@link com.fastlib.test.UrlImage.processing_state.StateDownloadImageIfExpire}
     * @param request 图像请求
     * @param imageView 要加载到的视图
     */
    private void internalAddBitmapRequest(final Activity activity, final BitmapRequest request, final ImageView imageView){
        Bitmap bitmapOnMemory=mBitmapReferenceManager.getFromMemory(request);
        if(bitmapOnMemory!=null){
            System.out.println("从内存中获取Bitmap:"+request.getUrl());
            imageView.setImageBitmap(bitmapOnMemory);
        }
        else{
            if(!mRequestList.contains(request)){
                final boolean bitmapOnDisk=BitmapRequest.getSaveFile(request).exists();

                mRequestList.add(request);
                final UrlImageProcessing urlImageProcessing=bitmapOnDisk?
                        new StateFirstLoadImageOnDisk(request,null):
                        new StateDownloadImageIfExpire(request,null);
                activity.getApplication().registerActivityLifecycleCallbacks(new AdapterActivityLifecycleCallbacks(){
                    @Override
                    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                        urlImageProcessing.onStart(activity);
                    }

                    @Override
                    public void onActivityPaused(Activity activity) {
                        urlImageProcessing.onPause(activity);
                    }

                    @Override
                    public void onActivityDestroyed(Activity activity) {
                        urlImageProcessing.onDestroy(activity);
                    }
                });
                NetManager.sRequestPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if(bitmapOnDisk)
                                mDiskLoaderQueue.put(urlImageProcessing);
                            else mNetDownloaderQueue.put(urlImageProcessing);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }


    /**
     * 返回图像请求列表
     * @return 图像请求列表
     */
    public List<BitmapRequest> getRequestList(){
        return mRequestList;
    }
}