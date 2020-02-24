package com.fastlib.net2;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.net2.core.HeaderDefinition;
import com.fastlib.net2.core.ResponseHeader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;

/**
 * Created by sgfb on 2020\02\20.
 * 多线程分块下载
 * 提示：一般不限制下载速度的资源使用单线程和多线程是差不多的,所以应优先使用单线程下载,遇到限速的资源再考虑使用多线程下载
 * 应当注意有些下载资源是没有总长度的,目前不支持这种资源多线程下载
 */
public class MultiSegmentDownloadController extends SimpleDownloadController{
    private final static int DEFAULT_THREAD_COUNT =Math.max(2,ThreadPoolManager.sSlowPool.getPoolSize()/2);
    private final static int BUFFER_SIZE=4096;
    private int mStartPoint;

    public MultiSegmentDownloadController(@NonNull File targetFile,int startPoint){
        this(targetFile,false,false,startPoint);
    }

    public MultiSegmentDownloadController( @NonNull File targetFile,boolean useServerFilename, boolean append,int startPoint){
        super(targetFile,useServerFilename,append);
        mStartPoint=startPoint;
    }

    @Override
    protected void onDownloadReady(final File toFile, InputStream inputStream, @Nullable String filename, final long length) throws IOException {
        RandomAccessFile randomAccessFile=new RandomAccessFile(toFile.getAbsolutePath(),"rw");
        randomAccessFile.setLength(length);
        if(mStartPoint>0)
            randomAccessFile.skipBytes(mStartPoint-1);

        byte[] buffer=new byte[BUFFER_SIZE];
        int len;
        while((len=inputStream.read(buffer))!=-1)
            randomAccessFile.write(buffer,0,len);
        randomAccessFile.close();
    }

    /**
     * 本方法只能在工作线程调起
     */
    public static final void startMultiDownload(Request request,File file) throws Exception {
        if(Thread.currentThread()==Looper.getMainLooper().getThread())
            throw new IllegalStateException("多线程下载只能在工作线程中启动");
        Request requestHead=request;
        requestHead.setMethod("head");
        requestHead.startSyc(String.class);

        ResponseHeader header=requestHead.getResponseHeader();
        long length=-1;
        if (header != null) {
            try{
                length=Long.parseLong(header.getHeaderFirst(HeaderDefinition.KEY_CONTENT_LENGTH));
            }catch (NumberFormatException e){
                //不处理
            }
        }

        if(length!=-1){
            final CountDownLatch latch=new CountDownLatch(DEFAULT_THREAD_COUNT);
            for(int i = 0, startPoint, endPoint = 0; i< DEFAULT_THREAD_COUNT; i++){
                startPoint=endPoint;
                if(startPoint>0)
                    startPoint+=1;
                endPoint+=length/ DEFAULT_THREAD_COUNT;

                System.out.println("start:"+startPoint+",end:"+endPoint);
                Request segmentRequest=new Request(request.getUrl());
                segmentRequest.addHeader("Range","bytes=" + startPoint + "-" + endPoint);
                segmentRequest.setDownloadable(new MultiSegmentDownloadController(file,startPoint));
                segmentRequest.setListener(new SimpleListener<String>(){

                    @Override
                    public void onResponseSuccess(Request request, String result) {
                        latch.countDown();
                    }
                });
                segmentRequest.start();
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
