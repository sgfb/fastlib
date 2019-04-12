package com.fastlib;

import android.content.Intent;
import android.text.format.Formatter;
import android.widget.EditText;

import com.fastlib.alpha.ClientRecordActivity;
import com.fastlib.alpha.ServerMonitorActivity;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.annotation.Event;
import com.fastlib.app.module.FastActivity;
import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.bean.event.EventDownloading;
import com.fastlib.net.DefaultDownload;
import com.fastlib.net.Request;
import com.fastlib.net.listener.SimpleListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Locale;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{

    @Override
    public void alreadyPrepared(){

    }

    @Bind(R.id.bt)
    private void startServer(){
        ThreadPoolManager.sSlowPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File file=new File(getExternalCacheDir(),"tcpTest.rar");
                    if(!file.exists())
                        file.createNewFile();

                    long timer=System.currentTimeMillis();
                    Socket socket=new Socket("192.168.1.122",2888);
                    InputStream in=socket.getInputStream();
                    OutputStream out=new FileOutputStream(file);
                    byte[] buffer=new byte[4096*2];
                    int len;
                    while((len=in.read(buffer))!=-1)
                        out.write(buffer,0,len);
                    in.close();
                    out.close();
                    socket.close();
                    System.out.println(String.format(Locale.getDefault(),"consume time:%d",System.currentTimeMillis()-timer));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Bind(R.id.bt2)
    private void startClient(){
        File file=new File(getExternalCacheDir(),"tcpTest.rar");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Request request=new Request("get","http://192.168.1.122:8080/FastProject/upload/BrorrowWalletSourceCode.rar");
        request.setDownloadable(new DefaultDownload(file));
        final long timer=System.currentTimeMillis();
        request.setListener(new SimpleListener<String>(){

            @Override
            public void onResponseListener(Request r, String result) {
                System.out.println(String.format(Locale.getDefault(),"consume time:%d",System.currentTimeMillis()-timer));
            }
        });
        request.start();
    }

    @Event
    private void eDownload(EventDownloading eventDownloading){
        System.out.println("speed:"+Formatter.formatFileSize(this,eventDownloading.getSpeed()));
    }
}