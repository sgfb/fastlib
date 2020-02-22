package com.fastlib.aspect.event_callback;

/**
 * Created by sgfb on 2020\02\22.
 * 三参数事件接收器
 */
public interface ThirdParamReceiver<T1,T2,T3> extends EventReceiver {

    void receiveEvent(T1 param1, T2 param2, T3 param3);
}
