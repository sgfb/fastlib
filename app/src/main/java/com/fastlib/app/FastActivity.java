package com.fastlib.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.fastlib.bean.PermissionRequest;
import com.fastlib.net.Request;
import com.fastlib.utils.ImageUtil;
import com.fastlib.utils.N;
import com.fastlib.utils.ViewInject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by sgfb on 16/9/5.
 * Activity基本封装
 */
public class FastActivity extends AppCompatActivity{
    private boolean isGetingPhoto; //是否正在获取图像
    private Thread mMainThread;
    private Map<String,PermissionRequest> mPermissionMap=new HashMap<>();
    private PhotoResultListener mPhotoResultListener;
    private List<Request> mRequests;
    protected ThreadPoolExecutor mThreadPool= (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mRequests=new ArrayList<>();
        mMainThread =Thread.currentThread();
        mThreadPool.execute(new Runnable(){
            @Override
            public void run(){
                registerEvents();
            }
        });
    }

    /**
     * 启动网络请求
     * @param request
     */
    protected void net(Request request){
        if(!mRequests.contains(request))
            mRequests.add(request);
        request.setHost(this).setExecutor(mThreadPool).start(false);
    }

    public void addRequest(Request request){
        if(!mRequests.contains(request))
            mRequests.add(request);
    }

    /**
     * 启动一个任务链
     * @param tc
     */
    protected void startTasks(TaskChain tc){
        TaskChain.processTaskChain(this,mThreadPool, mMainThread,tc);
    }

    /**
     * 开启获取相册照片
     * @param photoResultListener
     */
    protected void openAlbum(final PhotoResultListener photoResultListener){
        requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, new Runnable() {
            @Override
            public void run() {
                isGetingPhoto=true;
                mPhotoResultListener=photoResultListener;
                ImageUtil.openAlbum(FastActivity.this);
            }
        }, new Runnable() {
            @Override
            public void run() {
                N.showShort(FastActivity.this,"请开启读存储卡权限");
            }
        });
    }

    /**
     * 开启相机获取照片并且指定存储位置
     * @param photoResultListener
     * @param path
     */
    protected void openCamera(final PhotoResultListener photoResultListener, final String path){
        requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, new Runnable() {
            @Override
            public void run(){
                requestPermission(Manifest.permission.CAMERA, new Runnable() {
                    @Override
                    public void run(){
                        isGetingPhoto=true;
                        mPhotoResultListener=photoResultListener;
                        if(TextUtils.isEmpty(path))
                            ImageUtil.openCamera(FastActivity.this);
                        else
                            ImageUtil.openCamera(FastActivity.this,Uri.fromFile(new File(path)));
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        N.showShort(FastActivity.this,"请开启使用照相机权限");
                    }
                });
            }
        }, new Runnable() {
            @Override
            public void run(){
                N.showShort(FastActivity.this,"请开启写存储卡权限");
            }
        });
    }

    /**
     * 开启相机获取照片
     * @param photoResultListener
     */
    protected void openCamera(PhotoResultListener photoResultListener){
        openCamera(photoResultListener,null);
    }

    /**
     * 6.0后请求权限
     * @param permission
     * @param grantedAfterProcess
     * @param deniedAfterProcess
     */
    protected void requestPermission(String permission,Runnable grantedAfterProcess,Runnable deniedAfterProcess){
        if(ContextCompat.checkSelfPermission(this,permission)== PackageManager.PERMISSION_GRANTED)
            grantedAfterProcess.run();
        else{
            if(!mPermissionMap.containsKey(permission)){
                int requestCode= mPermissionMap.size()+1;
                mPermissionMap.put(permission,new PermissionRequest(requestCode,grantedAfterProcess,deniedAfterProcess));
                ActivityCompat.requestPermissions(this, new String[]{permission},requestCode);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(isGetingPhoto){
            isGetingPhoto=false;
            if(resultCode!=Activity.RESULT_OK)
                return;
            Uri photoUri=ImageUtil.getImageFromActive(this,requestCode,resultCode,data);
            if(photoUri!=null){
                String photoPath=ImageUtil.getImagePath(this,photoUri);
                if(mPhotoResultListener!=null)
                    mPhotoResultListener.onPhotoResult(photoPath);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int i=0;i<permissions.length;i++){
            PermissionRequest pr= mPermissionMap.remove(permissions[i]);
            if(pr!=null){
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED)
                    pr.hadPermissionProcess.run();
                else
                    pr.deniedPermissionProcess.run();
                break;
            }
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ViewInject.inject(this,mThreadPool);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ViewInject.inject(this,mThreadPool);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        ViewInject.inject(this,mThreadPool);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        EventObserver.getInstance().unsubscribe(this);
        mThreadPool.shutdownNow();
        mThreadPool.purge();
        for(Request request:mRequests)
            request.clear();
    }

    public void startActivity(Class<? extends Activity> cla){
        startActivity(new Intent(this,cla));
    }

    /**
     * 注册方法中的广播事件,如果有
     */
    private void registerEvents(){
        EventObserver.getInstance().subscribe(this);
    }

    /**
     * 请求相机或相册时图像回调接口
     */
    public interface PhotoResultListener{
        void onPhotoResult(String path);
    }
}
