package com.fastlib.adapter;

import android.support.annotation.Nullable;

/**
 * Created by sgfb on 2020\03\03.
 * 对另一端数据源请求数据时返回回调
 */
public interface OnRemoteRequestResultListener<T>{

    /**
     * 数据源正常返回
     */
    void onRemoteDataResult(@Nullable T resultData);

    /**
     * 数据源返回数据时出现异常
     */
    void onErrorResult(Exception e);
}
