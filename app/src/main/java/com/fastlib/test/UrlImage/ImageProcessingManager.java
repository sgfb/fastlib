package com.fastlib.test.UrlImage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
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
    private BlockingQueue<Runnable> mDiskLoaderQueue=new ArrayBlockingQueue<>(2);
    private BlockingQueue<Runnable> mNetDownloaderQueue=new ArrayBlockingQueue<>(2);

    public ImageProcessingManager(BitmapReferenceManager bitmapReferenceManager){
        mBitmapReferenceManager=bitmapReferenceManager;
        //长期占用两根线程作为服务器中数据下载到本地和本地读取到内存中的工作调度线程
        NetManager.sRequestPool.execute(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("ImageDiskLoader");
                while(true){
                    try {
                        Runnable runnable = mDiskLoaderQueue.take();
                        NetManager.sRequestPool.execute(runnable);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        NetManager.sRequestPool.execute(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("ImageNetDownloader");
                while(true){
                    try {
                        Runnable runnable = mNetDownloaderQueue.take();
                        NetManager.sRequestPool.execute(runnable);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 增加图像请求
     * @param request 图像请求
     * @param imageView 要加载到的视图
     */
    public void addBitmapRequest(final Context context,final BitmapRequest request, final ImageView imageView){
        Bitmap bitmapOnMemory=mBitmapReferenceManager.getFromMemory(request);
        if(bitmapOnMemory!=null){
            System.out.println("从内存中获取Bitmap:"+request.getUrl());
            imageView.setImageBitmap(bitmapOnMemory);
        }
        else{
            if(!mRequestList.contains(request)){
                mRequestList.add(request);
                //使用线程调度从服务器中拉取和磁盘中拉取到内存
                NetManager.sRequestPool.execute(new Runnable(){
                    @Override
                    public void run() {
                        startImageDownload(context,request,imageView);
                    }
                });
            }
        }
    }

    /**
     * 从磁盘中读取图像到内存
     * @param request 图像请求
     * @return Bitmap包裹
     */
    private BitmapWrapper loadImageWrapperOnDisk(BitmapRequest request){
        System.out.println("从磁盘读取图像到内存:"+request.getUrl());
        File file=BitmapRequest.getSaveFile(request);
        BitmapFactory.Options options=new BitmapFactory.Options();
        BitmapFactory.Options justDecodeBoundOptions=new BitmapFactory.Options();

        justDecodeBoundOptions.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(file.getAbsolutePath(),justDecodeBoundOptions);
        //如果请求宽高非0，尝试读取指定宽高中的最低值等比缩小。非0则尝试读取比手机屏幕小的尺寸
        if(request.getRequestWidth()!=0&&request.getRequestHeight()!=0){
            float widthRadio=justDecodeBoundOptions.outWidth/request.getRequestWidth();
            float heightRadio=justDecodeBoundOptions.outHeight/request.getRequestHeight();
            float maxRadio=Math.max(widthRadio,heightRadio);

            if(maxRadio>1)
                options.inSampleSize= (int) maxRadio;
        }
        else{
            Pair<Integer,Integer> screenSize=ScreenUtils.getScreenSize();

            if(justDecodeBoundOptions.outWidth>screenSize.first||justDecodeBoundOptions.outHeight>screenSize.second){
                float widthRadio=justDecodeBoundOptions.outWidth/screenSize.first;
                float heightRadio=justDecodeBoundOptions.outHeight/screenSize.second;
                float maxRadio=Math.max(widthRadio,heightRadio);

                options.inSampleSize= (int) maxRadio;
            }
        }
        options.inPreferredConfig=request.getBitmapConfig();
        BitmapWrapper wrapper=new BitmapWrapper();

        wrapper.mSampleSize=options.inSampleSize;
        wrapper.mBitmap=BitmapFactory.decodeFile(file.getAbsolutePath(),options);
        return wrapper;
    }

    private void startImageDownload(final Context context, final BitmapRequest bitmapRequest, final ImageView imageView){
        final Request request=new Request("get",bitmapRequest.getUrl());
        DefaultDownload dd=new DefaultDownload(BitmapRequest.getSaveFile(bitmapRequest));
        StringTable lastModified= FastDatabase.getDefaultInstance(context)
                .addFilter(And.condition(Condition.equal(bitmapRequest.getKey())))
                .getFirst(StringTable.class);

        if(lastModified!=null&&!TextUtils.isEmpty(lastModified.value)) {
            request.addHeader("If-Modified-Since", lastModified.value);
            System.out.println("验证服务器图像过期,如果过期重新在服务器上取:"+bitmapRequest.getUrl());
        }
        else System.out.println("从服务器中取图像到磁盘:"+bitmapRequest.getUrl());
        request.setDownloadable(dd);
        request.setSuppressWarning(true);
        request.setListener(new SimpleListener<String>(){

            @Override
            public void onResponseListener(Request r, String result){
                List<String> lastModifiedList=r.getReceiveHeader().get("Last-Modified");
                String lastModified=lastModifiedList==null||lastModifiedList.isEmpty()?"":lastModifiedList.get(0);

                mRequestList.remove(bitmapRequest);
                addLoadBitmapOnDiskRequest(bitmapRequest,imageView);
                saveImageLastModified(context,bitmapRequest.getKey(),lastModified);
            }

            @Override
            public void onErrorListener(Request r, String error){
                mRequestList.remove(bitmapRequest);
                if(r.getResponseStatus().code==304)
                    addLoadBitmapOnDiskRequest(bitmapRequest, imageView);
            }
        });
        request.start();
    }

    private void saveImageLastModified(Context context,String key,String lastModified){
        StringTable st=new StringTable();
        st.key=key;
        st.value=lastModified;
        FastDatabase.getDefaultInstance(context).saveOrUpdateAsync(st, new DatabaseNoDataResultCallback() {
            @Override
            public void onResult(boolean success) {
                //测试功能，还没想好异步存数据库后要干啥
            }
        });
    }

    private void addLoadBitmapOnDiskRequest(final BitmapRequest request, final ImageView imageView){
        try {
            mDiskLoaderQueue.put(new Runnable(){
                @Override
                public void run() {
                    Handler handler=new Handler(Looper.getMainLooper());
                    final BitmapWrapper wrapper= loadImageWrapperOnDisk(request);

                    mRequestList.remove(request);
                    mBitmapReferenceManager.addBitmapReference(request,wrapper,imageView);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(wrapper.mBitmap);
                        }
                    });
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}