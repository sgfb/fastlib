package com.fastlib.aspect.event_callback;

/**
 * Created by sgfb on 2020\02\22.
 * 单参数事件接收器
 */
public interface SingleParamReceiver<T1> extends EventReceiver{

    void receiveEvent(T1 param1);
}
