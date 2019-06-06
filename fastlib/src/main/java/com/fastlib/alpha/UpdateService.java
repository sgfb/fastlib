package com.fastlib.alpha;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.fastlib.app.EventObserver;
import com.fastlib.bean.event.EventDownloading;
import com.fastlib.net.DefaultDownload;
import com.fastlib.net.Request;
import com.fastlib.net.listener.SimpleListener;

import java.io.File;

/**
 * Created by Administrator on 2018/4/2.
 * 更新服务
 */
public abstract class UpdateService extends Service{
    public static final String ARG_STR_NAME="name";
    public static final String ARG_STR_UPDATE_URL="updateUrl";
    public static final String ACTION_CLOSE="closeAction";
    public static final String ACTION_START_PAUSE="startOrPause";
    private static final int NOTIFY_ID=1;

    Request mRequest;
    DownloadEventReceiver mActionReceiver;
    protected Notification mNotification;

    protected abstract int getLogoId();

    /**
     * 指定LayoutId
     * @return 返回一个layoutId
     */
    protected abstract int getLayoutId();

    protected abstract void bindingView(RemoteViews remoteViews);

    /**
     * 下载进度回调
     * @param percent 百分比
     */
    protected abstract void downloadingProgress(int percent);

    @Override
    public void onCreate() {
        super.onCreate();
        mActionReceiver=new DownloadEventReceiver();
        IntentFilter intentFilter=new IntentFilter(ACTION_CLOSE);
        intentFilter.addAction(ACTION_START_PAUSE);
        registerReceiver(mActionReceiver,intentFilter);
        EventObserver.getInstance().subscribe(this,this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        String updateUrl=intent.getStringExtra(ARG_STR_UPDATE_URL);
        String name=intent.getStringExtra(ARG_STR_NAME);
        initView();
        requestDownload(updateUrl,name);
        return super.onStartCommand(intent, flags, startId);
    }

    private void initView(){
        RemoteViews rv=new RemoteViews(getPackageName(),getLayoutId());

        bindingView(rv);
        Notification.Builder builder=new Notification.Builder(this)
                .setSmallIcon(getLogoId())
                .setTicker("开始下载新版本")
                .setContent(rv);
        mNotification=builder.build();
    }

    private void requestDownload(String url,String name){
        if(mRequest!=null) return;
        final File tempFile=new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),name);
        final File apkFile=new File(tempFile.getAbsolutePath()+".apk");

        if(apkFile.exists()){
            installApk(apkFile);
            return;
        }
        NotificationManager nm= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(NOTIFY_ID,mNotification);
        mRequest=new Request("get",url)
                .setCustomRootAddress("")
                .setDownloadable(new DefaultDownload(tempFile).setDownloadSegment(true));
        mRequest.setIntervalSendFileTransferEvent(500);
        mRequest.setListener(new SimpleListener<String>(){

            @Override
            public void onResponseListener(Request r, String result){
                tempFile.renameTo(apkFile);
                installApk(apkFile);
            }

            @Override
            public void onErrorListener(Request r, Exception error) {
                super.onErrorListener(r, error);
                updateError(r.isCancel(),error);
                stopSelf();
            }
        });
        mRequest.start();
    }

    /**
     * 后期绑定错误回调
     * @param selfCancel 自己手动取消
     * @param msg 错误消息
     */
    protected void updateError(boolean selfCancel,Exception msg){}

    /**
     * 安装更新包
     * @param file 更新包文件
     */
    private void installApk(File file){
        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(Build.VERSION_CODES.M<Build.VERSION.SDK_INT){
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(FileProvider.getUriForFile(this, getPackageName()+".fileProvider",file),"application/vnd.android.package-archive");
        }
        else intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        startActivity(intent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManager nm= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(NOTIFY_ID);
        unregisterReceiver(mActionReceiver);
        EventObserver.getInstance().unsubscribe(this,this);
    }

    /**
     * 使用Event注解覆盖
     * @param event 下载事件
     */
    @CallSuper
    protected void downloadParser(EventDownloading event){
        if(event.getRequest()!=mRequest) return;
        long downloadedLength= new File(event.getPath()).length();
        int percent= (int) (downloadedLength*100/event.getMaxLength());
        downloadingProgress(percent);
        startForeground(NOTIFY_ID,mNotification);
    }

    class DownloadEventReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(TextUtils.equals(intent.getAction(),ACTION_CLOSE)){
                if(mRequest!=null) mRequest.cancel();
                stopSelf();
            }
            else if(TextUtils.equals(intent.getAction(),ACTION_START_PAUSE)){
                mRequest.cancel();
                mRequest=null;
            }
        }
    }
}
