package com.fastlib.utils.bind_view;

import com.fastlib.app.AsyncCallback;

/**
 * Created by sgfb on 2020\03\01.
 */
public interface OnBindViewCallback{

    Object invokeSync(Object... args);

    void invokeAsync(AsyncCallback callback, Object... args);
}
