package com.fastlib.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.fastlib.annotation.ContentView;
import com.fastlib.annotation.TransitionAnimation;
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
 * Created by sgfb on 16/9/5.
 * Activity基本封装.
 * 1.ContentView注解，Bind视图注解.
 * 2.全局事件注册和解注册(EventObserver)
 * 3.线程池及顺序任务列辅助方法(mThreadPool和startTasks(TaskChain))
 * 4.本地数据辅助（LocalData）
 * 5.相机相册调取（openAlbum(PhotoResultListener)和openCamera(PhotoResultListener))
 * 6.6.0权限获取辅助(mPermissionHelper)
 */
public abstract class FastActivity extends AppCompatActivity{
    protected ThreadPoolExecutor mThreadPool;
    protected PermissionHelper mPermissionHelper;
    protected volatile int mPreparedTaskRemain=3; //剩余初始化异步任务，当初始化异步任务全部结束时调用alreadyPrepared

    private boolean isGatingPhoto; //是否正在获取图像
    private boolean isHadTransitionAnimation=false;
    private Thread mMainThread;
    private LocalDataInject mLocalDataInject;
    private PhotoResultListener mPhotoResultListener;
    private List<Request> mRequests = new ArrayList<>();

    protected abstract void alreadyPrepared(); //所有初始化任务结束

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mMainThread = Thread.currentThread();
        mPermissionHelper=new PermissionHelper();
        mLocalDataInject=new LocalDataInject(this);
        mThreadPool=generateThreadPool();
        checkTransitionInject();
        checkContentViewInject();
        mThreadPool.execute(new Runnable(){
            @Override
            public void run() {
                EventObserver.getInstance().subscribe(FastActivity.this,FastActivity.this);
                prepareTask();
            }
        });
    }

    /**
     * 后期绑定mThreadPool增加灵活性
     * @return 线程池
     */
    protected ThreadPoolExecutor generateThreadPool(){
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
    }

    /**
     * 查看是否有共享元素动画
     */
    private void checkTransitionInject(){
        TransitionAnimation ta=getClass().getAnnotation(TransitionAnimation.class);
        if(ta!=null){
            supportPostponeEnterTransition(); //暂停共享元素动画
            isHadTransitionAnimation=true;
        }
    }

    /**
     * ContentView注入，如果存在的话
     */
    private void checkContentViewInject(){
        ContentView cv=getClass().getAnnotation(ContentView.class);
        if(cv!=null)
            setContentView(cv.value());
    }


    /**
     * 启动网络请求
     * @param request 网络请求
     */
    protected void net(Request request) {
        if (!mRequests.contains(request))
            mRequests.add(request);
        request.setHost(this).setExecutor(mThreadPool).start(false);
    }

    /**
     * 增加网络请求到列表中，但是不立即请求
     * @param request 网络请求
     */
    public void addRequest(Request request) {
        if (!mRequests.contains(request))
            mRequests.add(request);
    }

    /**
     * 启动一个任务链
     * @param tc 任务链
     */
    public void startTasks(TaskChain tc){
//        TaskChainHead.processTaskChain(this, mThreadPool, mMainThread,tc.getFirst());
    }

    /**
     * 开启获取相册照片
     * @param photoResultListener 取相册中相片回调
     */
    protected void openAlbum(final PhotoResultListener photoResultListener) {
        mPermissionHelper.requestPermission(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new Runnable() {
            @Override
            public void run() {
                isGatingPhoto = true;
                mPhotoResultListener = photoResultListener;
                ImageUtil.openAlbum(FastActivity.this);
            }
        }, new Runnable() {
            @Override
            public void run() {
                N.showShort(FastActivity.this, "请开启读存储卡权限");
            }
        });
    }

    /**
     * 开启相机获取照片并且指定存储位置
     * @param photoResultListener 照相成功后回调
     * @param path 指定路径,这个路径的文件不能已被创建
     */
    protected void openCamera(final PhotoResultListener photoResultListener, final String path) {
        mPermissionHelper.requestPermission(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new Runnable() {
            @Override
            public void run() {
                mPermissionHelper.requestPermission(FastActivity.this,new String[]{Manifest.permission.CAMERA}, new Runnable() {
                    @Override
                    public void run() {
                        isGatingPhoto = true;
                        mPhotoResultListener = photoResultListener;
                        if (TextUtils.isEmpty(path))
                            ImageUtil.openCamera(FastActivity.this);
                        else
                            ImageUtil.openCamera(FastActivity.this,Uri.fromFile(new File(path)));
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        N.showShort(FastActivity.this, "请开启使用照相机权限");
                    }
                });
            }
        }, new Runnable() {
            @Override
            public void run() {
                N.showShort(FastActivity.this, "请开启写存储卡权限");
            }
        });
    }

    /**
     * 开启相机获取照片
     * @param photoResultListener 拍照成功回调
     */
    protected void openCamera(PhotoResultListener photoResultListener) {
        openCamera(photoResultListener, null);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLocalDataInject.injectChildBack(data);
        if (isGatingPhoto) {
            isGatingPhoto = false;
            if (resultCode != Activity.RESULT_OK)
                return;
            Uri photoUri = ImageUtil.getImageFromActive(this, requestCode, resultCode, data);
            if (photoUri != null) {
                String photoPath = ImageUtil.getImagePath(this, photoUri);
                if (mPhotoResultListener != null)
                    mPhotoResultListener.onPhotoResult(photoPath);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.permissionResult(requestCode,permissions,grantResults);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        afterSetContentView();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        afterSetContentView();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        afterSetContentView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventObserver.getInstance().unsubscribe(this,this);
        mThreadPool.shutdownNow();
        mThreadPool.purge();
        mThreadPool=null;
        for (Request request : mRequests)
            request.clear();
        mRequests.clear();
        mRequests=null;
    }

    /**
     * 在设置布局后做几个必要动作
     */
    protected void afterSetContentView(){
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                ViewInject.inject(FastActivity.this,findViewById(android.R.id.content),mThreadPool);
                prepareTask();
            }
        });
        mThreadPool.execute(new Runnable() {
            @Override
            public void run(){
                mLocalDataInject.localDataInject();
                prepareTask();
            }
        });
    }

    /**
     * 快捷启动一个Activity
     * @param cla 启动的Activity类
     */
    public void startActivity(Class<? extends Activity> cla) {
        startActivity(new Intent(this, cla));
    }

    /**
     * 快捷启动一个Activity并且获取返回
     * @param cla 启动的Activity类
     * @param requestCode 请求码
     */
    public void startActivityForResult(Class<? extends Activity> cla,int requestCode){
        startActivityForResult(new Intent(this,cla),requestCode);
    }

    /**
     * 其中一个异步任务完成
     */
    protected synchronized void prepareTask(){
        if(--mPreparedTaskRemain<=0)
            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    alreadyPrepared();
                    if(isHadTransitionAnimation)
                        supportStartPostponedEnterTransition();
                }
            });
    }
}