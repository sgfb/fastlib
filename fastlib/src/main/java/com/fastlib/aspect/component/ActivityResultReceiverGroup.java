package com.fastlib.aspect.component;

import android.content.Intent;

import com.fastlib.aspect.event_callback.EventReceiveGroup;

/**
 * Created by sgfb on 2020\02\22.
 * 对{@link android.app.Activity#onActivityResult(int, int, Intent)}事件接收组
 */
public class ActivityResultReceiverGroup extends EventReceiveGroup<Integer,Integer,Intent> {
}
