package com.fastlib;

import android.Manifest;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.annotation.Event;
import com.fastlib.app.module.FastActivity;
import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.bean.event.EventDownloading;
import com.fastlib.image_manager.request.Callback2ImageView;
import com.fastlib.image_manager.request.ImageRequest;
import com.fastlib.net.DefaultDownload;
import com.fastlib.net.Request;
import com.fastlib.net.listener.SimpleListener;
import com.fastlib.utils.ContextHolder;
import com.fastlib.utils.monitor.MonitorService;

import java.io.File;
import java.util.Locale;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    @Bind(R.id.list)
    RecyclerView mList;
    @Bind(R.id.image)
    ImageView mImage;
    @Bind(R.id.progress)
    ProgressBar mProgress;
    final String mImageUrl="http://cn.best-wallpaper.net/wallpaper/3840x2160/1705/Earth-our-home-planet-space-black-background_3840x2160.jpg";
//    final String mImageUrl="https://static.oschina.net/uploads/img/201901/31055503_3yCJ.png";
    Runnable mDownloadWatchdog=new Runnable() {
        @Override
        public void run() {
            while(!isComplete){
                System.out.println(String.format(Locale.getDefault(),"download:%dkb",mLastDownloaded/1024));
                mLastDownloaded=0;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void alreadyPrepared(){
        ContextHolder.init(getApplicationContext());
        requestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new Runnable() {
            @Override
            public void run() {

            }
        }, new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Bind(R.id.bt)
    private void bt(){
//        mList.setAdapter(new MyAdapter());
        isComplete=false;
        ThreadPoolManager.sSlowPool.execute(mDownloadWatchdog);
        final File file=new File(getExternalCacheDir(),"appium.exe");
        final String url="http://192.168.1.140:8080/Fastlib/upload/appium.exe";
        new Request("head",url)
                .setListener(new SimpleListener<String>(){

                    @Override
                    public void onResponseListener(Request r, String result) {
                        long length=Long.parseLong(r.getReceiveHeader().get("Content-Length").get(0));
                        new Request("get",url)
                                .setDownloadable(new DefaultDownload(file).setDownloadSegment(0,length/2))
                                .setListener(new SimpleListener<String>(){

                                    @Override
                                    public void onResponseListener(Request r, String result) {
                                        System.out.println(r.getDownloadable().getTargetFile().getAbsolutePath());
                                    }
                                })
                                .start();
                        new Request("get","http://192.168.1.103:8080/Fastlib/upload/appium.exe")
                                .setDownloadable(new DefaultDownload(file).setDownloadSegment(length/2,length))
                                .setListener(new SimpleListener<String>(){

                                    @Override
                                    public void onResponseListener(Request r, String result) {
                                        System.out.println(r.getDownloadable().getTargetFile().getAbsolutePath());
                                    }
                                })
                                .start();
                    }
                }).start();
    }

    @Bind(R.id.bt2)
    private void bt2(){
        isComplete=false;
        ThreadPoolManager.sSlowPool.execute(mDownloadWatchdog);
        final File file=new File(getExternalCacheDir(),"appium.exe");
        final String url="http://192.168.1.140:8080/Fastlib/upload/appium.exe";
        new Request("get",url).setDownloadable(new DefaultDownload(file)).start();
//        ImageRequest.create(mImageUrl)
//                .bindOnHostLifeCycle(this)
//                .setCallbackParcel(new Callback2ImageView(mImage))
//                .start();
    }

    @Bind(R.id.bt3)
    private void bt3(){
        isComplete=false;
        ThreadPoolManager.sSlowPool.execute(mDownloadWatchdog);
        final File file=new File(getExternalCacheDir(),"appium.exe");
        final String url="http://192.168.1.103:8080/Fastlib/upload/appium.exe";
        new Request("get",url).setDownloadable(new DefaultDownload(file)).start();
//        startService(new Intent(this, MonitorService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    boolean isComplete=false;
    long mLastDownloaded=0;

    @Event
    private void eDownload(EventDownloading event){
        long currSize=new File(event.getPath()).length();
        mLastDownloaded+=event.getSpeed();
        mProgress.setProgress((int) (currSize*100/event.getMaxLength()));
    }
}