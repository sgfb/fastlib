package com.fastlib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.EventObserver;
import com.fastlib.app.FastActivity;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by sgfb on 17/6/26.
 */
@ContentView(R.layout.act_main_2)
public class SecondActivity extends FastActivity implements WifiP2pManager.PeerListListener{
    @Bind(R.id.list)
    ListView mList;
    WifiP2PDisplayAdapter mAdapter;
    boolean isWifiP2PEnable=false;
    WifiP2pManager.Channel mChannel;
    WifiP2pManager mP2pManger;
    BroadcastReceiver mWifiP2PReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent){
            String action=intent.getAction();
            if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){ //Wifi环境变更广播
                WifiP2pInfo p2pInfo=intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
                NetworkInfo netInfo=intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                System.out.println("WifiP2PInfo:"+p2pInfo);
                System.out.println("network info:"+netInfo);
                if(Build.VERSION.SDK_INT>=18){
                    WifiP2pGroup p2pGroup=intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);
                    System.out.println("wifi p2p group:"+p2pGroup);
                }
            }
            else if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){ //指示Wifi p2p的状态
                int wifiP2PStatus=intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);
                isWifiP2PEnable=wifiP2PStatus==WifiP2pManager.WIFI_P2P_STATE_ENABLED;
                System.out.println("wifi p2p 状态变更:"+wifiP2PStatus);
            }
            else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
                mP2pManger.requestPeers(mChannel,SecondActivity.this);
                if(Build.VERSION.SDK_INT>=18){
                    WifiP2pDeviceList dl=intent.getParcelableExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST);
                    if(dl.getDeviceList().isEmpty()) System.out.println("wifi 直连列表为空");
                    else{
                        Collection<WifiP2pDevice> collection=dl.getDeviceList();
                        List<WifiP2pDevice> list=new ArrayList<>();
                        for(WifiP2pDevice device:collection)
                            list.add(device);
                        mAdapter.setData(list);
                    }
                }
                else
                    System.out.println("wifi 直连列表变更，但是系统版本过低，无法靠广播读取");
            }
            else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){ //本机P2P设置变更
                System.out.println("本机P2P设置变更");
            }
        }
    };

    @Bind(R.id.startServer)
    private void startServer(){
        try {
            ServerSocket ss=new ServerSocket(2888);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Bind(R.id.bt1)
    private void searchWifiP2PDevice(){
        mP2pManger.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                System.out.println("启动Wifi P2P 搜索成功");
            }

            @Override
            public void onFailure(int reason) {
                System.out.println("启动Wifi P2P 搜索失败:"+reason);
            }
        });
    }

    @Bind(R.id.bt2)
    private void stopWifiP2pDevice(){
        mP2pManger.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                System.out.println("关闭Wifi P2P 搜索成功");
            }

            @Override
            public void onFailure(int reason) {
                System.out.println("关闭Wifi P2P 搜索失败:"+reason);
            }
        });
    }

    @Override
    protected void alreadyPrepared(){
        mList.setAdapter(mAdapter=new WifiP2PDisplayAdapter(this));
        mP2pManger= (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        mChannel=mP2pManger.initialize(this,getMainLooper(),null);
        IntentFilter filter=new IntentFilter(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        registerReceiver(mWifiP2PReceiver,filter);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mWifiP2PReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        EventObserver.build(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers){
        System.out.println(peers);
    }
}