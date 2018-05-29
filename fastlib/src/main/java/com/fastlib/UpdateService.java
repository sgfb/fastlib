package com.fastlib;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.e.applibrary.bean.CommonInterface;
import com.e.applibrary.bean.Response;
import com.e.applibrary.bean.ResponseUpdate;
import com.erector.applibrary.BuildConfig;
import com.erector.applibrary.R;
import com.fastlib.annotation.Event;
import com.fastlib.app.EventObserver;
import com.fastlib.app.FastDialog;
import com.fastlib.bean.event.EventDownloading;
import com.fastlib.net.DefaultDownload;
import com.fastlib.net.Request;
import com.fastlib.net.SimpleListener;
import com.fastlib.utils.N;

import java.io.File;
import java.util.Locale;

/**
 * Created by Administrator on 2018/4/2.
 * 更新服务
 */
public class UpdateService extends Service{
    public static final String ARG_STR_UPDATE_URL="updateUrl";
    public static final String ARG_STR_VERSION_NAME="versionName";
    public static final String ACTION_CLOSE="closeAction";
    Request mRequest;
    DownloadEventReceiver mActionReceiver;
    Notification mNotification;

    @Override
    public void onCreate() {
        super.onCreate();
        mActionReceiver=new DownloadEventReceiver();
        registerReceiver(mActionReceiver,new IntentFilter(ACTION_CLOSE));
        EventObserver.getInstance().subscribe(this,this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        String versionName=intent.getStringExtra(ARG_STR_VERSION_NAME);
        String updateUrl=intent.getStringExtra(ARG_STR_UPDATE_URL);

        initView(versionName);
        init(updateUrl,versionName);
        return super.onStartCommand(intent, flags, startId);
    }

    private void initView(String versionName){
        RemoteViews rv=new RemoteViews(getPackageName(), R.layout.ser_update);
        rv.setTextViewText(R.id.title,String.format(Locale.getDefault(),"正在为您更新至%s版本",versionName));
        rv.setOnClickPendingIntent(R.id.close,PendingIntent.getBroadcast(this,1,new Intent(ACTION_CLOSE),PendingIntent.FLAG_UPDATE_CURRENT));

        Notification.Builder builder=new Notification.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setTicker("开始下载新版本")
                .setContent(rv);
        NotificationManager nm= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(1,mNotification=builder.build());
    }

    private void init(String url,String versionName){
        if(mRequest!=null) return;
        final File tempFile=new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),String.format(Locale.getDefault(),"erector%s",versionName));
        final File apkFile=new File(tempFile.getAbsolutePath()+".apk");

        if(apkFile.exists()){
            installApk(apkFile);
            return;
        }
        mRequest=new Request("get",url);
        mRequest.setHadRootAddress(true);
        mRequest.setUseFactory(false);
        mRequest.setDownloadable(new DefaultDownload(tempFile).setSupportBreak(true));
        mRequest.setHost(this);
        mRequest.setIntervalSendFileTransferEvent(500);
        mRequest.setListener(new SimpleListener<String>(){

            @Override
            public void onResponseListener(Request r, String result) {
                tempFile.renameTo(apkFile);
                installApk(apkFile);
            }

            @Override
            public void onErrorListener(Request r, String error) {
                super.onErrorListener(r, error);
                if(r.isCancel()) N.showLong(UpdateService.this,"中断更新");
                else N.showLong(UpdateService.this,"更新异常");
                stopSelf();
            }
        });
        mRequest.start();
    }

    private void installApk(File file){
        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(Build.VERSION_CODES.N<=Build.VERSION.SDK_INT){
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
        nm.cancel(1);
        unregisterReceiver(mActionReceiver);
        EventObserver.getInstance().unsubscribe(this,this);
    }

    @Event
    private void eDownload(EventDownloading event){
        long downloadedLength= new File(event.getPath()).length();
        int percent= (int) (downloadedLength*100/event.getMaxLength());
        mNotification.contentView.setProgressBar(R.id.progress,100,percent,false);
        startForeground(1,mNotification);
    }

    class DownloadEventReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(TextUtils.equals(intent.getAction(),ACTION_CLOSE)){
                if(mRequest!=null) mRequest.cancel();
                stopSelf();
            }
        }
    }

    public static void startUpdateService(final AppCompatActivity context,int type,int versionCode){
        new Request(CommonInterface.INSTANCE.getPOST_UPDATE())
                .put("type",type)
                .put("version",versionCode)
                .setAcceptGlobalCallback(false)
                .setListener(new SimpleListener<Response<ResponseUpdate>>(){

                    @Override
                    public void onResponseListener(Request r, final Response<ResponseUpdate> result) {
                        if(result.getCode()==Response.CODE_SUCCESS){
                            FastDialog.showMessageDialog(result.getObj().getDesciption(),true)
                                    .setTitle("新版本")
                                    .show(context.getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent=new Intent(context,UpdateService.class);
                                            intent.putExtra(UpdateService.ARG_STR_UPDATE_URL,result.getObj().getUrl());
                                            intent.putExtra(UpdateService.ARG_STR_VERSION_NAME,result.getObj().getVersion()) ;
                                            context.startService(intent);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onErrorListener(Request r, String error) {
                        super.onErrorListener(r, error);
                        N.showShort(context,"服务器异常");
                    }
                }).start();
    }
}
