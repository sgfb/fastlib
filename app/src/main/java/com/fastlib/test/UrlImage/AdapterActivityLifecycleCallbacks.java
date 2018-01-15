package com.fastlib.test.UrlImage;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by sgfb on 18/1/15.
 * Activity生命周期适配
 */
public class AdapterActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks{

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        //被适配
    }

    @Override
    public void onActivityStarted(Activity activity) {
        //被适配
    }

    @Override
    public void onActivityResumed(Activity activity) {
        //被适配
    }

    @Override
    public void onActivityPaused(Activity activity) {
        //被适配
    }

    @Override
    public void onActivityStopped(Activity activity) {
        //被适配
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        //被适配
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        //被适配
    }
}
