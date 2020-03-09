package com.fastlib.utils.router;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import android.view.View;

/**
 * Created by sgfb on 2020\03\01.
 */
public class FragmentRouter<T> extends Router<T,Fragment> {

    public FragmentRouter(Class<T> routerLinkCla, Fragment mHost) {
        super(routerLinkCla, mHost);
    }

    @Override
    protected View findViewById(int id) {
        return mHost.getView().findViewById(id);
    }

    @Override
    protected Context getContext() {
        return mHost.getContext();
    }

    @Override
    protected void startActivity(Intent intent) {
        mHost.startActivity(intent);
    }

    @Override
    protected void startActivityForResult(Intent intent, int requestCode) {
        mHost.startActivityForResult(intent,requestCode);
    }
}
