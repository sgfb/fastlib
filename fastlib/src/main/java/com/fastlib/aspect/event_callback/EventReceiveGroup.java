package com.fastlib.aspect.event_callback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 2020\02\22.
 * 事件接收组
 * 有一群等待接收指定事件的组合
 */
public abstract class EventReceiveGroup<T1,T2,T3>{
    private List<ThirdParamReceiver<T1,T2,T3>> mEventCallbacks=new ArrayList<>();

    public void addEventCallback(ThirdParamReceiver<T1,T2,T3> eventCallback){
        mEventCallbacks.add(eventCallback);
    }

    public void sendEvent(T1 param1,T2 param2,T3 param3){
        for(ThirdParamReceiver<T1,T2,T3> eventCallback:mEventCallbacks)
            eventCallback.receiveEvent(param1,param2,param3);
        mEventCallbacks.clear();
    }
}
