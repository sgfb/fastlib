package com.fastlib.test.UrlImage;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;

import com.fastlib.test.UrlImage.request.BitmapRequest;

/**
 * Created by sgfb on 18/1/15.
 * 一个具有状态的网络图像处理
 */
public abstract class UrlImageProcessing implements HostLifecycle{
    protected BitmapRequest mRequest;
    protected ImageDispatchCallback mCallback;
    private Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks=new AdapterActivityLifecycleCallbacks(){

        @Override
        public void onActivityResumed(Activity activity) {
            onStart(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            onPause(activity);
        }

        @Override
        public void onActivityDestroyed(Activity activity){
            onDestroy(activity);
        }
    };

    public abstract void handle(ImageProcessManager processingManager);

    public UrlImageProcessing(BitmapRequest request, ImageDispatchCallback callback){
        mRequest = request;
        mCallback = callback;
        registerLifecycle();
    }

    @Override
    public void onStart(Context context) {
        //适配，不强制实现
    }

    @Override
    public void onPause(Context context) {
        //适配，不强制实现
    }

    @CallSuper
    @Override
    public void onDestroy(Context context) {
        unregisterLifecycle();
    }

    /**
     * 状态变更，与前状态交接
     * @param beforeProcessing 前状态
     */
    public void stateConvert(UrlImageProcessing beforeProcessing){
        beforeProcessing.unregisterLifecycle();
    }

    /**
     * 注册宿主生命周期
     */
    public void registerLifecycle(){
        Object host=mRequest.getHost();
        if(host!=null){
            if(host instanceof Activity){
                Activity activity= (Activity)host;
                activity.getApplication().registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
            }
            else if(host instanceof Fragment){
                Fragment fragment= (Fragment)host;
                LifecycleControlFragment controlFragment=new LifecycleControlFragment();
                controlFragment.setHostLifecycle(this);
                fragment.getChildFragmentManager()
                        .beginTransaction()
                        .add(controlFragment,"lifecycleControl")
                        .commit();
            }
        }
    }

    /**
     * 解注册宿主生命周期
     */
    public void unregisterLifecycle(){
        Object host=mRequest.getHost();

        if(host!=null){
            if(host instanceof Activity){
                Activity activity= (Activity) host;
                activity.getApplication().unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
            }
            else if(host instanceof Fragment){
                Fragment fragment=(Fragment)host;
                fragment.getFragmentManager()
                        .beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        }
    }
}