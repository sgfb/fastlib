package com.fastlib.utils.monitor;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.WindowManager;

import com.fastlib.utils.ScreenUtils;

/**
 * 监控服务.提供开发者调试时手机上更多性能和日志输出（仅针对应用内和fastLib库）
 * 1.cpu使用率
 * 2.内存使用率
 * 3.全局线程池状态
 * 4.网络请求队列状态
 */
public class MonitorService extends Service{
    private MonitorView mMonitorView;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        WindowManager wm= (WindowManager) getSystemService(WINDOW_SERVICE);
        if(Build.VERSION.SDK_INT>=23&&!Settings.canDrawOverlays(this)){
            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
            stopSelf();
        }
        else{
            mMonitorView=new MonitorView(this);
            wm.addView(mMonitorView.getView(),initLayoutParam());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private WindowManager.LayoutParams initLayoutParam(){
        WindowManager.LayoutParams lp=new WindowManager.LayoutParams();
        lp.x=0;
        lp.y=0;
        lp.gravity=Gravity.END|Gravity.TOP;
        lp.type= Build.VERSION.SDK_INT>=26?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        lp.flags=WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.format=PixelFormat.RGBA_8888;
        lp.height=ScreenUtils.getScreenHeight()/3;
        return lp;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mMonitorView!=null)
            mMonitorView.stop();
    }
}
