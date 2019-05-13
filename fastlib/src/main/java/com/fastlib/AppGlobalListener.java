package com.fastlib;

import com.fastlib.net.Request;
import com.fastlib.net.listener.GlobalListener;

/**
 * Create by sgfb on 2019/05/13
 * E-Mail:602687446@qq.com
 */
public class AppGlobalListener extends GlobalListener{

    @Override
    public void onLaunchRequestBefore(Request request) {
        super.onLaunchRequestBefore(request);
        System.out.println(request);
    }

    @Override
    public void onRequestLaunched(Request request) {
        super.onRequestLaunched(request);
    }
}
