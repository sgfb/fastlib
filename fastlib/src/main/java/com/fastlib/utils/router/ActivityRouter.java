package com.fastlib.utils.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.fastlib.R;

/**
 * Created by sgfb on 2020\03\01.
 */
public class ActivityRouter<T> extends Router<T,Activity>{

    public ActivityRouter(Class<T> mRouterLink, Activity mHost) {
        super(mRouterLink,mHost);
    }

    @Override
    protected View findViewById(int id) {
        return mHost.findViewById(id);
    }

    @Override
    protected Context getContext() {
        return mHost;
    }

    @Override
    protected void startActivity(Intent intent) {
        mHost.startActivity(intent);
    }

    @Override
    protected void startActivityForResult(Intent intent,int requestCode) {
        mHost.startActivityForResult(intent,requestCode);
    }
}
