package com.fastlib.app.module;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fastlib.app.PhotoResultListener;
import com.fastlib.app.task.EmptyAction;
import com.fastlib.app.task.NoReturnAction;
import com.fastlib.app.task.Task;
import com.fastlib.app.task.TaskLauncher;
import com.fastlib.net.Request;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by sgfb on 17/1/31.
 * Fragment基本封装
 * 1.全局事件注册和解注册（EventObserver）
 * 2.视图属性和事件注解（Bind）
 * 3.线程池及顺序任务列辅助方法(mThreadPool和startTasks(Task))
 * 4.本地数据辅助（LocalData）
 * 5.相机相册调取（openAlbum(PhotoResultListener)和openCamera(PhotoResultListener))
 * 6.6.0权限获取辅助(mPermissionHelper)
 * 7.延时启动优化
 */
public abstract class FastFragment extends Fragment implements ModuleInterface {
    private ModuleDelegate mDelegate;
    private Pair<Integer,View> mContentView;

    //----------------------------继承自Fragment系列-------------------------------------//
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDelegate=new ModuleDelegate(this,this);
        created();
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        if(mContentView!=null){
            if(mContentView.second!=null) return mContentView.second;
            return inflater.inflate(mContentView.first,null);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDelegate.afterSetContentView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mDelegate.onModuleHandleActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mDelegate.onHandlePermissionResult(requestCode,permissions,grantResults);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyed();
    }

    //----------------------------继承自Module系列-------------------------------------//
    @Override
    public void alreadyContentView(@LayoutRes int layoutId) {
        mContentView=Pair.create(layoutId,null);
    }

    @Override
    public void alreadyContentView(View view) {
        mContentView=Pair.create(-1,view);
    }

    @Override
    public void openAlbum(PhotoResultListener photoResultListener) {
        mDelegate.openAlbum(photoResultListener);
    }

    @Override
    public void openCamera(PhotoResultListener photoResultListener, String path) {
        mDelegate.openCamera(photoResultListener,path);
    }

    @Override
    public void openCamera(PhotoResultListener photoResultListener) {
        mDelegate.openCamera(photoResultListener);
    }

    @Override
    public void loading() {
        mDelegate.loading();
    }

    @Override
    public void loading(String hint) {
        mDelegate.loading(hint);
    }

    @Override
    public void dismissLoading() {
        mDelegate.dismissLoading();
    }

    @Override
    public TaskLauncher startTask(Task task) {
        return mDelegate.startTask(task);
    }

    @Override
    public TaskLauncher startTask(Task task, NoReturnAction<Throwable> exceptionHandler, EmptyAction lastAction) {
        return mDelegate.startTask(task,exceptionHandler,lastAction);
    }

    @Override
    public void net(Request request) {
        mDelegate.net(request);
    }

    @Override
    public View generateDeferLoadingView() {
        return null;
    }

    @Override
    public void requestPermission(String[] permission, Runnable grantedAfterProcess, Runnable deniedAfterProcess) {
        mDelegate.requestPermission(permission,grantedAfterProcess,deniedAfterProcess);
    }

    @Override
    public ThreadPoolExecutor getThreadPool() {
        return mDelegate.getThreadPool();
    }

    @Override
    public View getRootView() {
        return getView();
    }

    @Override
    public void created() {
        mDelegate.created();
    }

    @Override
    public void destroyed() {
        mDelegate.destroyed();
    }

    @Override
    public void firstLoad() {
        mDelegate.firstLoad();
    }

    @Override
    public void setRefreshStatus(boolean status) {
        mDelegate.setRefreshStatus(status);
    }

    @Override
    public void setRefreshCallback(OnRefreshCallback callback) {
        mDelegate.setRefreshCallback(callback);
    }
}