package com.fastlib.app.module;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.fastlib.app.ActivityLifecycleCallbacksAdapter;

/**
 * Created by sgfb on 2018/5/18.
 */
public class LifecycleManager{

    private LifecycleManager(){

    }

    public static void registerLifecycle(final Object host, final FastActivity.HostLifecycle lifecycle){
        Application.ActivityLifecycleCallbacks activityLifecycleCallbacks=new ActivityLifecycleCallbacksAdapter(){

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
                }
            }
        };
        if(host!=null){
            if(host instanceof Activity){
                Activity activity= (Activity)host;
                activity.getApplication().registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
            }
            else if(host instanceof Fragment){
                Fragment fragment= (Fragment)host;
                ModuleInterface.LifecycleControlFragment controlFragment=new ModuleInterface.LifecycleControlFragment();
                controlFragment.setHostLifecycle(new FastActivity.HostLifecycle() {
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
                    }
                });
                fragment.getChildFragmentManager()
                        .beginTransaction()
                        .add(controlFragment,"lifecycleControl")
                        .commit();
            }
            else throw new IllegalArgumentException("host must be activity or fragment");
        }
        else throw new IllegalArgumentException("host can't be null");
    }
}