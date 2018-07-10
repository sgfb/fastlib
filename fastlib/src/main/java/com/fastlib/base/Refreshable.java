package com.fastlib.base;

/**
 * Created by sgfb on 16/9/21.
 * 实现此接口表示实现类支持刷新功能
 */
public interface Refreshable{

    /**
     * 设置刷新状态
     * @param status true开始刷新false关闭刷新
     */
    void setRefreshStatus(boolean status);

    /**
     * 设置刷新被触发回调
     * @param callback 刷新触发回调
     */
    void setRefreshCallback(OnRefreshCallback callback);

    interface OnRefreshCallback {
        void startRefresh();
    }
}
