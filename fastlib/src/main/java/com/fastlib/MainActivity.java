package com.fastlib;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.utils.N;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    final String mImageUrl="http://cn.best-wallpaper.net/wallpaper/3840x2160/1705/Earth-our-home-planet-space-black-background_3840x2160.jpg";
//    final String mImageUrl="https://static.oschina.net/uploads/img/201901/31055503_3yCJ.
    @Bind(R.id.address)
    EditText mAddress;
    @Bind(R.id.clientPort)
    EditText mClientPort;
    @Bind(R.id.port)
    EditText mPort;

    @Override
    public void alreadyPrepared(){

    }

    @Bind(R.id.bt)
    private void startServer(){
        ThreadPoolManager.sQuickPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Process process=Runtime.getRuntime().exec("cat /sys/class/power_supply/battery/current_now");
                    BufferedReader in=new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while(!TextUtils.isEmpty((line=in.readLine()))) {
                        System.out.println("battery current:"+line);
                        final String finalLine = line;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                N.showLong(MainActivity.this, finalLine);
                            }
                        });
                    }
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
//        String port=mPort.getText().toString().trim();
//        if(TextUtils.isEmpty(port)){
//            N.showLong(this,"端口不能空");
//            return;
//        }
//        startActivity(new Intent(this,ServerMonitorActivity.class)
//        .putExtra(ServerMonitorActivity.ARG_INT_PORT,Integer.parseInt(port)));
    }

    @Bind(R.id.bt2)
    private void startClient(){
        String port=mClientPort.getText().toString().trim();
        String address=mAddress.getText().toString().trim();
        if(TextUtils.isEmpty(port)||TextUtils.isEmpty(address)){
            N.showLong(this,"端口和地址不能空");
            return;
        }
        startActivity(new Intent(this,ClientRecordActivity.class)
        .putExtra(ClientRecordActivity.ARG_STR_ADDRESS,address)
        .putExtra(ClientRecordActivity.ARG_INT_PORT,Integer.parseInt(port)));
    }
}