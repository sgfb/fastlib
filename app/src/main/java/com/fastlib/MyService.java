package com.fastlib;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by sgfb on 17/12/18.
 * 守护进程，主要测试主程序奔溃后是否能收集Crash信息
 */
public class MyService extends Service{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
