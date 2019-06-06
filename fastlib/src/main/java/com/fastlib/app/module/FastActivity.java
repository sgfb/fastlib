package com.fastlib.app.module;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.fastlib.app.PhotoResultListener;
import com.fastlib.app.task.EmptyAction;
import com.fastlib.app.task.NoReturnAction;
import com.fastlib.app.task.Task;
import com.fastlib.app.task.TaskLauncher;
import com.fastlib.net.Request;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by sgfb on 16/9/5.
 * Activity基本封装.
 */
public abstract class FastActivity extends AppCompatActivity implements ModuleInterface {
    private ModuleDelegate mDelegate=new ModuleDelegate(this,this);

    //----------------------------继承自Activity系列-------------------------------------//
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
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

    @Override
    public void onBackPressed() {
        boolean handled=false;
        List<Fragment> fragmentList=getSupportFragmentManager().getFragments();
        //越后面的fragment是越后面加入的所以需要反向触发
        for(int i=fragmentList.size()-1;i>0;i--){
            Fragment fragment=fragmentList.get(i);
            if(fragment instanceof SupportBack){
                if(((SupportBack) fragment).onBackPressed()){
                    handled=true;
                    break;
                }
            }
        }
        if(!handled)
            super.onBackPressed();
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

    @Override
    public ModuleLife getModuleLife() {
        return mDelegate.getModuleLife();
    }

    /**
     * Created by sgfb on 18/1/15.
     * 宿主生命周期回调监听
     */
    public interface HostLifecycle{

        /**
         * 开始生命周期,相当于onResume
         * @param context 对应宿主上下文
         */
        void onStart(Context context);

        /**
         * 仅退回到后台，不在前台运行但并未被销毁
         * @param context 对应宿主上下文
         */
        void onPause(Context context);

        /**
         * 被销毁，最后处理机会
         * @param context 对应宿主上下文
         */
        void onDestroy(Context context);
    }
}