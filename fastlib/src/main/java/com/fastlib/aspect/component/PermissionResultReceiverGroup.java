package com.fastlib.aspect.component;

import com.fastlib.aspect.event_callback.EventReceiveGroup;

/**
 * Created by sgfb on 2020\02\22.
 * 对{@link android.app.Activity#onRequestPermissionsResult(int, String[], int[])}事件接收组
 */
public class PermissionResultReceiverGroup extends EventReceiveGroup<Integer,String[],int[]>{

}
