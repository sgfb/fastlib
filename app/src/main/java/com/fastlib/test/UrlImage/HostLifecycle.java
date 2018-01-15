package com.fastlib.test.UrlImage;

import android.content.Context;

/**
 * Created by sgfb on 18/1/15.
 * 宿主生命周期回调监听
 */
public interface HostLifecycle{

    /**
     * 开始生命周期
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