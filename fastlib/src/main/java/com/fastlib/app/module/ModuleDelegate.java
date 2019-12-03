package com.fastlib.app.module;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;

import com.fastlib.annotation.ContentView;
import com.fastlib.app.EventObserver;
import com.fastlib.app.LoadingDialog;
import com.fastlib.app.PhotoResultListener;
import com.fastlib.app.task.EmptyAction;
import com.fastlib.app.task.NoReturnAction;
import com.fastlib.app.task.Task;
import com.fastlib.app.task.TaskLauncher;
import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.app.task.ThreadType;
import com.fastlib.net.NetManager;
import com.fastlib.net.Request;
import com.fastlib.utils.ImageUtil;
import com.fastlib.utils.N;
import com.fastlib.utils.PermissionHelper;
import com.fastlib.utils.Reflect;
import com.fastlib.utils.ViewInject;
import com.fastlib.utils.local_data.LocalDataInject;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by sgfb on 18/7/17.
 * 1.ContentView注解，Bind视图注解.
 * 2.全局事件注册和解注册 {@link EventObserver}
 * 3.线程池及顺序任务列辅助方法 {@link #startTask(Task)}
 * 4.本地数据辅助 {@link com.fastlib.annotation.LocalData}
 * 5.相机相册调取 {@link #openAlbum(PhotoResultListener)}和{@link #openCamera(PhotoResultListener)}
 * 6.6.0权限获取辅助 {@link PermissionHelper}
 * 7.延时启动优化
 */
public class ModuleDelegate implements ModuleInterface {
    protected ThreadPoolExecutor mThreadPool;
    protected PermissionHelper mPermissionHelper;
    protected ModuleLife mLife=new ModuleLife();

    private boolean isFirstLoaded = false;
    private boolean isGatingPhoto;                      //是否正在获取图像
    private LocalDataInject mLocalDataInject;
    private PhotoResultListener mPhotoResultListener;
    private LoadingDialog mLoading;
    private ViewStub mViewStub;
    private View mDeferView;
    private FragmentActivity mContext;
    private Fragment mFragmentContext;
    private ModuleInterface mHost;

    public ModuleDelegate(ModuleInterface host, Fragment fragment){
        mHost=host;
        mFragmentContext=fragment;
    }

    public ModuleDelegate(ModuleInterface host, FragmentActivity context){
        mHost=host;
        mContext=context;
    }

    /**
     * ContentView注入，如果存在的话
     */
    private void checkContentViewInject() {
        ContentView cv = Reflect.findAnnotation(mHost.getClass(),ContentView.class);
        if (cv != null) {
            if (mDeferView == null) alreadyContentView(cv.value());
            else {
                FrameLayout frameLayout=new FrameLayout(getRealActivity());
                mViewStub = new ViewStub(getRealActivity(), cv.value());
                frameLayout.addView(mViewStub);
                frameLayout.addView(mDeferView);
                alreadyContentView(frameLayout);
            }
        }
    }

    /**
     * 后期绑定mThreadPool增加灵活性
     * @return 线程池
     */
    protected ThreadPoolExecutor generateThreadPool() {
        return ThreadPoolManager.sQuickPool;
    }

    /**
     * 在设置布局后视图注解和局部数据注解
     */
    protected void afterSetContentView(){
        //先检查是否有延迟并且未显示
        if(mDeferView==null&&!isFirstLoaded)
            startInternalPrepareTask();
    }

    /**
     * 内部预任务
     * @return 额外的预任务
     */
    protected Task genPrepareTaskList() {
        return Task.begin(new EmptyAction() {
            @Override
            protected void executeAdapt() {
                ViewInject.inject(mHost, getRootView(), mThreadPool);
                mLocalDataInject.localDataInject();
                EventObserver.getInstance().subscribe(getRealActivity(),mHost);
            }
        });
    }

    /**
     * 开始内部任务加载
     */
    private void startInternalPrepareTask() {
        Task task = genPrepareTaskList();
        NoReturnAction<Throwable> exceptionAction=new NoReturnAction<Throwable>() {
            @Override
            public void executeAdapt(Throwable param) {
                param.printStackTrace();
            }
        };
        EmptyAction lastAction=new EmptyAction() {
            @Override
            protected void executeAdapt() {
                endInternalPrepareTask();
            }
        };
        lastAction.setThreadType(ThreadType.MAIN);

        if (task == null)
            endInternalPrepareTask();
        else {
            if (prepareOnMainThread()) {
                new TaskLauncher.Builder(mLife,mThreadPool)
                        .setExceptionHandler(exceptionAction)
                        .setLastTask(lastAction)
                        .setForceOnMainThread(true)
                        .build()
                        .startTask(task);
            } else startTask(task,exceptionAction,lastAction);
        }
    }

    private void endInternalPrepareTask() {
        if (mDeferView != null) {
            if (mDeferView.getParent() instanceof ViewGroup) {
                ViewGroup parent = (ViewGroup) mDeferView.getParent();
                parent.removeView(mDeferView);
            }
        }
        alreadyPrepared();
        mLocalDataInject.toggleDelayLocalDataMethod();
    }

    @Override
    public void alreadyContentView(@LayoutRes int layoutId) {
        mHost.alreadyContentView(layoutId);
    }

    @Override
    public void alreadyContentView(View view) {
        mHost.alreadyContentView(view);
    }

    @Override
    public void alreadyPrepared() {
        mHost.alreadyPrepared();
    }

    @Override
    public View getRootView() {
        return mHost.getRootView();
    }

    @Override
    public ModuleLife getModuleLife() {
        return mLife;
    }

    /**
     * 开启获取相册照片
     * @param photoResultListener 取相册中相片回调
     */
    @Override
    public void openAlbum(final PhotoResultListener photoResultListener) {
        requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new Runnable() {
            @Override
            public void run() {
                isGatingPhoto = true;
                mPhotoResultListener = photoResultListener;
                if(mFragmentContext!=null) ImageUtil.openAlbum(mFragmentContext);
                else ImageUtil.openAlbum(getRealActivity());
            }
        }, new Runnable() {
            @Override
            public void run() {
                N.showShort(getRealActivity(), "请开启读存储卡权限");
            }
        });
    }

    /**
     * 开启相机获取照片并且指定存储位置
     * @param photoResultListener 照相成功后回调
     * @param path                指定路径
     */
    @Override
    public void openCamera(final PhotoResultListener photoResultListener, final String path) {
        requestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new Runnable() {
            @Override
            public void run() {
                requestPermission(new String[]{Manifest.permission.CAMERA}, new Runnable() {
                    @Override
                    public void run() {
                        isGatingPhoto = true;
                        mPhotoResultListener = photoResultListener;
                        if (TextUtils.isEmpty(path)) {
                            if(mFragmentContext!=null)
                                ImageUtil.openCamera(mFragmentContext);
                            else ImageUtil.openCamera(getRealActivity());
                        }
                        else{
                            if(mFragmentContext!=null)
                                ImageUtil.openCamera(mFragmentContext,Uri.fromFile(new File(path)));
                            else ImageUtil.openCamera(getRealActivity(), Uri.fromFile(new File(path)));
                        }
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        N.showShort(getRealActivity(), "请开启使用照相机权限");
                    }
                });
            }
        }, new Runnable() {
            @Override
            public void run() {
                N.showShort(getRealActivity(), "请开启写存储卡权限");
            }
        });
    }

    /**
     * 开启相机获取照片
     * @param photoResultListener 拍照成功回调
     */
    @Override
    public void openCamera(PhotoResultListener photoResultListener) {
        openCamera(photoResultListener, null);
    }

    public void onModuleHandleActivityResult(int requestCode, int resultCode, Intent data) {
        mLocalDataInject.injectChildBack(data);
        if (isGatingPhoto) {
            isGatingPhoto = false;
            if (resultCode != Activity.RESULT_OK)
                return;
            Uri photoUri = ImageUtil.getImageFromActive(getRealActivity(), requestCode, resultCode, data);
            if (photoUri != null) {
                String photoPath = ImageUtil.getImagePath(getRealActivity(), photoUri);
                if (mPhotoResultListener != null)
                    mPhotoResultListener.onPhotoResult(photoPath);
            }
        }
    }

    public void onHandlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        mPermissionHelper.permissionResult(requestCode,permissions,grantResults);
    }

    /**
     * 显示进度条
     */
    @Override
    public void loading() {
        loading("请稍后...");
    }

    /**
     * 显示无限进度条
     * @param hint 进度提示
     */
    @Override
    public void loading(final String hint) {
        if (mLoading == null)
            mLoading = new LoadingDialog();
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mLoading.show(getRealActivity().getSupportFragmentManager());
                mLoading.setHint(hint);
            }
        });
    }

    /**
     * 关闭进度条
     */
    @Override
    public void dismissLoading() {
        if (mLoading != null) {
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    mLoading.dismiss();
                }
            });
        }
    }

    /**
     * 开始线性任务
     * @param task 任务
     */
    public TaskLauncher startTask(Task task) {
        return startTask(task, null, null);
    }

    /**
     * 开始线性任务，并且有异常处理和尾回调
     * @param task             任务
     * @param exceptionHandler 异常处理
     * @param lastAction       尾回调
     */
    @Override
    public TaskLauncher startTask(Task task, NoReturnAction<Throwable> exceptionHandler, EmptyAction lastAction) {
        TaskLauncher taskLauncher = new TaskLauncher.Builder(mLife,mThreadPool)
                .setExceptionHandler(exceptionHandler)
                .setLastTask(lastAction)
                .build();
        taskLauncher.startTask(task);
        return taskLauncher;
    }

    /**
     * 启动网络请求
     * @param request 网络请求
     */
    @Override
    public void net(Request request) {
        request.setHostLifecycle(mLife)
                .start(false);
    }

    /**
     * 将某事件运行在主线程中
     * @param runnable 事件
     */
    private void runOnMainThread(Runnable runnable) {
        if (Looper.getMainLooper() != Looper.myLooper())
            getRealActivity().runOnUiThread(runnable);
        else runnable.run();
    }

    /**
     * 延迟加载视图，如果不为空，使用延迟加载策略
     * @return 延迟加载视图
     */
    @Override
    public View generateDeferLoadingView() {
        return mHost.generateDeferLoadingView();
    }

    @Override
    public void requestPermission(String[] permission, Runnable grantedAfterProcess, Runnable deniedAfterProcess) {
        mPermissionHelper.requestPermission(getRealActivity(),mFragmentContext,permission,grantedAfterProcess,deniedAfterProcess);
    }
    /**
     * 内部任务运行线程
     * @return true强制在主线程中运行预任务, false无限制
     */
    protected boolean prepareOnMainThread() {
        return false;
    }

    @Override
    public void firstLoad() {
        if (!isFirstLoaded && mViewStub != null) {
            isFirstLoaded = true;
            mViewStub.inflate();
            startInternalPrepareTask();
        }
    }

    @Override
    public void setRefreshStatus(boolean status) {
        if(status) loading();
        else dismissLoading();
    }

    @Override
    public void setRefreshCallback(OnRefreshCallback callback) {
        //Module的loading不会被手动触发
    }

    @Override
    public void created() {
        mLife.flag=ModuleLife.LIFE_CREATED;
        mDeferView = generateDeferLoadingView();
        mPermissionHelper = new PermissionHelper();
        mLocalDataInject =new LocalDataInject(mHost);
        mThreadPool = generateThreadPool();
        checkContentViewInject();
    }

    @Override
    public void destroyed() {
        mLife.flag=ModuleLife.LIFE_DESTROYED;
        EventObserver.getInstance().unsubscribe(getRealActivity(),mHost);
    }

    @Override
    public ThreadPoolExecutor getThreadPool() {
        return mThreadPool;
    }

    private FragmentActivity getRealActivity(){
        return mContext!=null?mContext:mFragmentContext.getActivity();
    }
}