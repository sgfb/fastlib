package com.fastlib.uncomplete.monitors;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.WindowManager;

import com.fastlib.utils.DensityUtils;

/**
 * Created by sgfb on 17/1/29.
 * cpu监控服务.需要权限SYSTEM_ALERT_WINDOW
 */
public class CpuRateMonitorService extends Service{
    CpuMonitor monitor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        WindowManager.LayoutParams lp=new WindowManager.LayoutParams();
        WindowManager wm= (WindowManager) getSystemService(WINDOW_SERVICE);
        final CpuRateView floatView=new CpuRateView(this);
        int cpuCount=Runtime.getRuntime().availableProcessors();

        floatView.setCpuCount(cpuCount);
        lp.x=0;
        lp.y=0;
        lp.gravity= Gravity.RIGHT|Gravity.TOP;
        lp.type=WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        lp.flags=WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.height= DensityUtils.dp2px(this,100);
        lp.format= PixelFormat.RGBA_8888;
        new Thread(){
            @Override
            public void run(){
                monitor=new CpuMonitor(1000,new CpuMonitor.CpuRateCallback(){
                    @Override
                    public void onCallback(final CpuMonitor.SingleCpuInfo[] info){
                        final float[] cpuRate=new float[info.length];
                        for(int i=0;i<cpuRate.length;i++)
                            cpuRate[i]=info[i].rate;
                        floatView.post(new Runnable() {
                            @Override
                            public void run() {
                                floatView.setPercent(cpuRate);
                            }
                        });
                    }
                },true);
            }
        }.start();
        wm.addView(floatView,lp);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(monitor!=null)
            monitor.stop();
    }
}
