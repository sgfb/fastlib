package com.fastlib.demo.app;

import com.fastlib.demo.base.RemoteAdapterBindView;
import com.fastlib.demo.base.SwipeRefreshBindView;
import com.fastlib.utils.bind_view.ViewInject;

/**
 * Created by sgfb on 2020\03\03.
 */
public final class CustomViewInject {
    public static final String TYPE_REMOTE_ADAPTER_ALL ="remoteAdapterAll";
    public static final String TYPE_SWIPE_REFRESH="swipeRefreshRefresh";

    private CustomViewInject(){}

    public static void inflaterCustomViewInject(){
        ViewInject.putBindViewReceiver(TYPE_SWIPE_REFRESH,SwipeRefreshBindView.class);
        ViewInject.putBindViewReceiver(TYPE_REMOTE_ADAPTER_ALL,RemoteAdapterBindView.class);
    }
}
