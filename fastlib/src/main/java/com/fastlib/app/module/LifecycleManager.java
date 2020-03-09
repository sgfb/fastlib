package com.fastlib.app.module;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import androidx.fragment.app.Fragment;

import com.fastlib.app.ActivityLifecycleCallbacksAdapter;

import java.lang.ref.WeakReference;
import java.util.Locale;

/**
 * Created by sgfb on 2018/5/18.
 */
public class LifecycleManager{
    private WeakReference<Object> mHost;
    private Application.ActivityLifecycleCallbacks mActivityLifecycle;
    private ModuleInterface.LifecycleControlFragment mControlFragment;

    public void unregisterLifecycle(){
        if(mHost!=null&&mHost.get()!=null){
            Object host=mHost.get();
            if(host instanceof Activity){
                ((Activity)host).getApplication().unregisterActivityLifecycleCallbacks(mActivityLifecycle);
            }
            else if(host instanceof Fragment){
                ((Fragment)host).getChildFragmentManager()
                        .beginTransaction()
                        .remove(mControlFragment)
                        .commit();
            }
            mHost.clear();
            mHost=null;
            mActivityLifecycle=null;
            mControlFragment=null;
        }
    }

    /**
     * 绑定组件生命周期
     * @param host      activity或fragment
     * @param lifecycle 周期回调
     */
    public void registerLifecycle(final Object host, final FastActivity.HostLifecycle lifecycle){
        mHost=new WeakReference<>(host);
        mActivityLifecycle=new ActivityLifecycleCallbacksAdapter(){

            @Override
            public void onActivityResumed(Activity activity) {
                if(host==activity)
                    lifecycle.onStart(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if(host==activity)
                    lifecycle.onPause(activity);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if(host==activity){
                    lifecycle.onDestroy(activity);
                    activity.getApplication().unregisterActivityLifecycleCallbacks(this);
                    if(mHost!=null){
                        mHost.clear();
                        mHost=null;
                    }
                }
            }
        };
        if(host!=null){
            if(host instanceof Activity){
                Activity activity= (Activity)host;
                activity.getApplication().registerActivityLifecycleCallbacks(mActivityLifecycle);
            }
            else if(host instanceof Fragment){
                final Fragment fragment= (Fragment)host;
                mControlFragment=new ModuleInterface.LifecycleControlFragment();
                mControlFragment.setHostLifecycle(new FastActivity.HostLifecycle() {
                    @Override
                    public void onStart(Context context) {
                        lifecycle.onStart(context);
                    }

                    @Override
                    public void onPause(Context context) {
                        lifecycle.onPause(context);
                    }

                    @Override
                    public void onDestroy(Context context) {
                        lifecycle.onDestroy(context);
                        fragment.getChildFragmentManager()
                                .beginTransaction()
                                .remove(mControlFragment)
                                .commit();
                        if(mHost!=null){
                            mHost.clear();
                            mHost=null;
                        }
                    }
                });
                fragment.getChildFragmentManager()
                        .beginTransaction()
                        .add(mControlFragment,String.format(Locale.getDefault(),"lifecycleControl_%s",mControlFragment))
                        .commit();
            }
            else throw new IllegalArgumentException("host must be activity or fragment");
        }
        else throw new IllegalArgumentException("host can't be null");
    }
}