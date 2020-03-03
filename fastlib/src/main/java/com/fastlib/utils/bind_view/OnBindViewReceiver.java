package com.fastlib.utils.bind_view;

import android.view.View;

import com.fastlib.annotation.Bind;

/**
 * Created by sgfb on 2020\03\01.
 * 视图绑定行为事件持有者
 */
public interface OnBindViewReceiver {

    /**
     * 给予原生监听间接触发绑定事件
     * @param callback 绑定事件
     */
    void setOnBindViewCallback(OnBindViewCallback callback);

    /**
     * 绑定视图监听.例 View.setOnClickListener(Listener)
     * @param view  对应{@link Bind#value()}得到的视图
     */
    void bindView(View view);
}
