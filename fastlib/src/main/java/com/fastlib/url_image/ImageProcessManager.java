package com.fastlib.url_image;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.fastlib.net.NetManager;
import com.fastlib.url_image.bean.BitmapWrapper;
import com.fastlib.url_image.bean.FastImageConfig;
import com.fastlib.url_image.callback.ImageDispatchCallback;
import com.fastlib.url_image.pool.TargetReference;
import com.fastlib.url_image.processing.StateCheckImagePrepare;
import com.fastlib.url_image.processing.StateLoadImageOnResource;
import com.fastlib.url_image.processing.StateLoadNewImageOnDisk;
import com.fastlib.url_image.processing.UrlImageProcessing;
import com.fastlib.url_image.request.BitmapRequest;
import com.fastlib.url_image.request.DiskBitmapRequest;
import com.fastlib.url_image.request.ResourceBitmapRequest;
import com.fastlib.url_image.request.UrlBitmapRequest;

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
public class ImageProcessManager {
    private TargetReference mImageViewReference;
    private List<BitmapRequest> mRequestList=new ArrayList<>();
    private BlockingQueue<UrlImageProcessing> mDiskLoaderQueue=new ArrayBlockingQueue<>(3);
    private BlockingQueue<UrlImageProcessing> mNetDownloaderQueue=new ArrayBlockingQueue<>(3);

    public ImageProcessManager(TargetReference imageViewReference){
        mImageViewReference = imageViewReference;
        //长期占用两根线程作为服务器中数据下载到本地和本地读取到内存中处理工作(后期改为工作线程调度)
        NetManager.sRequestPool.execute(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("ImageDiskLoader");
                try{
                    while(true){
                        UrlImageProcessing processing = mDiskLoaderQueue.take();
                        processing.handle(ImageProcessManager.this);
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
                        processing.handle(ImageProcessManager.this);
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
     * @param oldProcessing 已完成的处理状态
     * @param newProcessing 即将开始的处理状态
     */
    public void imageProcessStateConvert(boolean diskState,UrlImageProcessing oldProcessing,UrlImageProcessing newProcessing){
        try{
            newProcessing.stateConvert(oldProcessing);
            if(diskState) mDiskLoaderQueue.put(newProcessing);
            else mNetDownloaderQueue.put(newProcessing);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    /**
     * 试图增加一个网络请求到队列
     * @param request 网络请求
     */
    public void addBitmapRequest(BitmapRequest request){
        final UrlImageProcessing urlImageProcessing=generateFirstImageProcessing(request);
        internalAddBitmapRequest(request,urlImageProcessing);
    }

    /**
     * 根据图像请求位置和缓存状态来生成处理器
     * @param request 图像请求
     * @return 图像请求处理器
     */
    private UrlImageProcessing generateFirstImageProcessing(BitmapRequest request){
        ImageDispatchCallback callback=new ImageDispatchCallback() {
            @Override
            public void complete(UrlImageProcessing processing, final BitmapRequest request, final BitmapWrapper wrapper){
                Handler handler=new Handler(Looper.getMainLooper());
                if(wrapper.bitmap!=null){
                    //根据策略来是否存储到内存池，和缓存在磁盘上
                    if((request.getStoreStrategy()& FastImageConfig.STRATEGY_STORE_SAVE_MEMORY)!=0)
                        mImageViewReference.addBitmapReference(request,wrapper,request.getTarget());
                    else if(((request.getStoreStrategy()&FastImageConfig.STRATEGY_STORE_SAVE_DISK)==0)&&request.getSaveFile()!=null)
                        request.getSaveFile().delete();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run(){
                        request.completeRequest(wrapper.bitmap);
                    }
                });
            }
        };
        //先检查是否请求内部,文件是否存在磁盘上
        if(request instanceof ResourceBitmapRequest)
            return new StateLoadImageOnResource(request,callback);
        else if(request instanceof DiskBitmapRequest)
            return new StateLoadNewImageOnDisk(request,callback);
        else if(request instanceof UrlBitmapRequest)
            return new StateCheckImagePrepare(request,callback);
        else return null;
    }

    /**
     * 增加图像请求.最初过滤，如果图片存在内存中直接取内存
     * 如果存在磁盘中调起{@link StateCheckImagePrepare}
     * 否则直接调起{@link com.fastlib.url_image.processing.StateDownloadImageIfExpire}
     * @param request 图像请求
     */
    private void internalAddBitmapRequest(final BitmapRequest request,final UrlImageProcessing urlImageProcessing){
        Bitmap bitmapOnMemory= mImageViewReference.getFromMemory(request);
        if(bitmapOnMemory!=null){
            System.out.println("从内存中获取Bitmap:"+request.getResource());
            request.completeRequest(bitmapOnMemory);
            urlImageProcessing.unregisterLifecycle();
        }
        else{
            if(!mRequestList.contains(request)){
                mRequestList.add(request);
                NetManager.sRequestPool.execute(new Runnable(){
                    @Override
                    public void run() {
                        try{
                            if(request instanceof UrlBitmapRequest&&(request.getSaveFile()==null||!request.getSaveFile().exists()))
                                mNetDownloaderQueue.put(urlImageProcessing);
                            else
                                mDiskLoaderQueue.put(urlImageProcessing);
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