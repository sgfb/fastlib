package com.fastlib.app.module;

import android.support.annotation.LayoutRes;
import android.view.View;

import com.fastlib.app.PhotoResultListener;
import com.fastlib.app.task.EmptyAction;
import com.fastlib.app.task.NoReturnAction;
import com.fastlib.app.task.Task;
import com.fastlib.app.task.TaskLauncher;
import com.fastlib.base.Deferrable;
import com.fastlib.base.Refreshable;
import com.fastlib.net.Request;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by sgfb on 18/7/17.
 * 为模块化组件提供一组一致的接口
 * 1.可延迟加载
 * 2.可刷新
 * 3.生命周期控制
 * 4.相机相册调取
 * 5.忙碌状态loading
 * 6.线程池和Task系列封装
 * 7.6.0运行时权限请求
 */
public interface Module extends Deferrable,Refreshable,ModuleLifecycle{

    void alreadyContentView(@LayoutRes int layoutId);

    void alreadyContentView(View view);

    void alreadyPrepared();

    void openAlbum(final PhotoResultListener photoResultListener);

    void openCamera(final PhotoResultListener photoResultListener, final String path);

    void openCamera(PhotoResultListener photoResultListener);

    void loading();

    void loading(final String hint);

    void dismissLoading();

    TaskLauncher startTask(Task task);

    TaskLauncher startTask(Task task, NoReturnAction<Throwable> exceptionHandler, EmptyAction lastAction);

    void net(Request request);

    View generateDeferLoadingView();

    void requestPermission(String[] permission, Runnable grantedAfterProcess, Runnable deniedAfterProcess);

    ThreadPoolExecutor getThreadPool();

    View getRootView();
}