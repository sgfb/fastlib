package com.fastlib.app.module;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
 * Created by sgfb on 16/9/5.
 * Activity基本封装.
 */
public abstract class FastActivity extends AppCompatActivity implements ModuleInterface {
    private ModuleDelegate mDelegate=new ModuleDelegate(this,this);

    //----------------------------继承自Activity系列-------------------------------------//
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        created();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mDelegate.onModuleHandleActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mDelegate.onHandlePermissionResult(requestCode,permissions,grantResults);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        mDelegate.afterSetContentView();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        mDelegate.afterSetContentView();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        mDelegate.afterSetContentView();
    }

    //----------------------------继承自Module系列-------------------------------------//

    @Override
    public void alreadyContentView(@LayoutRes int layoutId) {
        setContentView(layoutId);
    }

    @Override
    public void alreadyContentView(View view) {
        setContentView(view);
    }

    @Override
    public View getRootView() {
        return findViewById(android.R.id.content);
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
    public ThreadPoolExecutor getThreadPool() {
        return mDelegate.getThreadPool();
    }

    @Override
    public void destroyed() {
        mDelegate.destroyed();
    }

    @Override
    public void created() {
        mDelegate.created();
    }

    @Override
    public void firstLoad() {
        mDelegate.firstLoad();
    }

    @Override
    public void setRefreshCallback(OnRefreshCallback callback) {
        mDelegate.setRefreshCallback(callback);
    }

    @Override
    public void setRefreshStatus(boolean status) {
        mDelegate.setRefreshStatus(status);
    }

    @Override
    public void requestPermission(String[] permission, Runnable grantedAfterProcess, Runnable deniedAfterProcess) {
        mDelegate.requestPermission(permission,grantedAfterProcess,deniedAfterProcess);
    }
}