package com.fastlib.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fastlib.annotation.ContentView;
import com.fastlib.net.Request;
import com.fastlib.utils.ImageUtil;
import com.fastlib.utils.LocalDataInject;
import com.fastlib.utils.N;
import com.fastlib.utils.PermissionHelper;
import com.fastlib.utils.ViewInject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by sgfb on 17/1/31.
 * Fragment基本封装
 * 1.全局事件注册和解注册（EventObserver）
 * 2.视图属性和事件注解（Bind）
 * 3.线程池及顺序任务列辅助方法(mThreadPool和startTasks(TaskChain))
 * 4.本地数据辅助（LocalData）
 * 5.相机相册调取（openAlbum(PhotoResultListener)和openCamera(PhotoResultListener))
 * 6.6.0权限获取辅助(mPermissionHelper)
 */
public abstract class FastFragment extends Fragment{
    protected ThreadPoolExecutor mThreadPool;
    protected PermissionHelper mPermissionHelper;

    private boolean isGatingPhoto; //是否正在获取图像
    private volatile int mPreparedTaskRemain=2; //剩余初始化异步任务，当初始化异步任务全部结束时调用alreadyPrepared
    private List<Request> mRequests=new ArrayList<>();
    private LocalDataInject mLocalDataInject;
    private PhotoResultListener mPhotoResultListener;

    protected abstract void alreadyPrepared(); //所有初始化任务结束

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocalDataInject=new LocalDataInject(this);
        mPermissionHelper=new PermissionHelper(getActivity());
        mThreadPool=(ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        mThreadPool.execute(new Runnable(){
            @Override
            public void run(){
                EventObserver.getInstance().subscribe(FastFragment.this);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        ContentView cv=getClass().getAnnotation(ContentView.class);
        if(cv!=null)
            return inflater.inflate(cv.value(),null);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                ViewInject.inject(FastFragment.this,mThreadPool);
                prepareTask();

            }
        });
        mThreadPool.execute(new Runnable(){
            @Override
            public void run() {
                mLocalDataInject.localDataInject();
                prepareTask();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        mLocalDataInject.injectChildBack(data); //尝试获取有注解的子Activity返回数据
        if (isGatingPhoto) {
            isGatingPhoto = false;
            if (resultCode != Activity.RESULT_OK)
                return;
            Uri photoUri = ImageUtil.getImageFromActive(getContext(), requestCode, resultCode, data);
            if (photoUri != null) {
                String photoPath = ImageUtil.getImagePath(getContext(), photoUri);
                if (mPhotoResultListener != null)
                    mPhotoResultListener.onPhotoResult(photoPath);
            }
        }
    }

    /**
     * 开启获取相册照片
     * @param photoResultListener
     */
    protected void openAlbum(final PhotoResultListener photoResultListener) {
        mPermissionHelper.requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, new Runnable() {
            @Override
            public void run() {
                isGatingPhoto = true;
                mPhotoResultListener = photoResultListener;
                ImageUtil.openAlbum(FastFragment.this);
            }
        }, new Runnable() {
            @Override
            public void run() {
                N.showShort(getContext(),"请开启读存储卡权限");
            }
        });
    }

    /**
     * 开启相机获取照片并且指定存储位置
     * @param photoResultListener
     * @param path
     */
    protected void openCamera(final PhotoResultListener photoResultListener,final String path) {
        mPermissionHelper.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, new Runnable() {
            @Override
            public void run() {
                mPermissionHelper.requestPermission(Manifest.permission.CAMERA, new Runnable() {
                    @Override
                    public void run() {
                        isGatingPhoto = true;
                        mPhotoResultListener = photoResultListener;
                        if (TextUtils.isEmpty(path))
                            ImageUtil.openCamera(FastFragment.this);
                        else
                            ImageUtil.openCamera(FastFragment.this,Uri.fromFile(new File(path)));
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        N.showShort(getContext(), "请开启使用照相机权限");
                    }
                });
            }
        }, new Runnable() {
            @Override
            public void run() {
                N.showShort(getContext(), "请开启写存储卡权限");
            }
        });
    }

    /**
     * 开启相机获取照片
     * @param photoResultListener
     */
    protected void openCamera(PhotoResultListener photoResultListener) {
        openCamera(photoResultListener, null);
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
    protected void startTasks(TaskChain tc) {
        TaskChainHead.processTaskChain(getActivity(),mThreadPool,Thread.currentThread(),tc.getFirst());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPreparedTaskRemain=2;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        EventObserver.getInstance().unsubscribe(this);
        mThreadPool.shutdownNow();
        mThreadPool.purge();
        for(Request request:mRequests)
            request.clear();
    }

    private synchronized void prepareTask(){
        if(--mPreparedTaskRemain<=0)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    alreadyPrepared();
                }
            });
    }
}