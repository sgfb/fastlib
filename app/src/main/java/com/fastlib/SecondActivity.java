package com.fastlib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.EventObserver;
import com.fastlib.app.FastActivity;
import com.fastlib.net.Request;
import com.fastlib.net.SimpleListener;
import com.fastlib.utils.N;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by sgfb on 17/6/26.
 */
@ContentView(R.layout.act_main_2)
public class SecondActivity extends FastActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        EventObserver.build(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void alreadyPrepared(){

    }

    @Bind(R.id.bt1)
    private void sendFile(){
        Request request=Request.obtain("http://www.hzhanghuan.com/api/v1/tools/uploadImage");
        request.put("file",new File(Environment.getExternalStorageDirectory(),"algorithms.jpg"));
        request.setListener(new SimpleListener<String>(){

            @Override
            public void onResponseListener(Request r,String result) {
                System.out.println("result:"+result);
            }

            @Override
            public void onErrorListener(Request r, String error) {
                super.onErrorListener(r, error);
                System.out.println("error:"+error);
            }
        });
        net(request);
    }
}