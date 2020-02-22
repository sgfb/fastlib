package com.fastlib.aspect.event_callback;

/**
 * Created by sgfb on 2020\02\22.
 * 双参数事件接收器
 */
public interface DoubleParamReceiver<T1,T2> extends EventReceiver {

    void receiveEvent(T1 param1, T2 param2);
}
