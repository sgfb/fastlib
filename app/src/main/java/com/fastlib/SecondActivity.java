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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.EventObserver;
import com.fastlib.app.FastActivity;
import com.fastlib.utils.N;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by sgfb on 17/6/26.
 */
@ContentView(R.layout.act_main_2)
public class SecondActivity extends FastActivity implements WifiP2pManager.PeerListListener,AdapterView.OnItemClickListener, WifiP2pManager.GroupInfoListener {
    @Bind(R.id.list)
    ListView mList;
    WifiP2PDisplayAdapter mAdapter;
    WifiP2pManager.Channel mChannel;
    WifiP2pManager mP2pManger;
    BroadcastReceiver mWifiP2PReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent){
            String action=intent.getAction();
            if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){ //Wifi环境变更广播
                WifiP2pInfo info=intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
                if(info!=null&&info.isGroupOwner){
                    Intent intent1=new Intent(SecondActivity.this,DetailActivity.class);
                    intent1.putExtra(DetailActivity.ARG_BOOL_SERVER,true);
                    startActivity(intent1);
                }
                else{
                    Intent intent1=new Intent(SecondActivity.this,DetailActivity.class);
                    intent1.putExtra(DetailActivity.ARG_BOOL_SERVER,false);
                    intent1.putExtra(DetailActivity.ARG_STR_HOST_NAME,info.groupOwnerAddress.getHostAddress());
                    startActivity(intent1);
                }
            }
            else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
                mP2pManger.requestPeers(mChannel,SecondActivity.this);
            }
        }
    };

    @Bind(R.id.startServer)
    private void startServer(){
        Intent intent=new Intent(this,DetailActivity.class);
        intent.putExtra(DetailActivity.ARG_BOOL_SERVER,true);
        startActivity(intent);
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
        mList.setOnItemClickListener(this);
        mP2pManger= (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        mChannel=mP2pManger.initialize(this,getMainLooper(),null);
        IntentFilter filter=new IntentFilter(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        registerReceiver(mWifiP2PReceiver,filter);
        mP2pManger.requestPeers(mChannel,this);
        mP2pManger.requestGroupInfo(mChannel,this);
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
        if(peers.getDeviceList().isEmpty()) N.showShort(this,"wifi 直连列表为空");
        else{
            Collection<WifiP2pDevice> collection=peers.getDeviceList();
            List<WifiP2pDevice> list=new ArrayList<>();
            for(WifiP2pDevice device:collection)
                list.add(device);
            mAdapter.setData(list);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        final WifiP2pDevice device=mAdapter.getItem(position);
        WifiP2pConfig config=new WifiP2pConfig();
        config.deviceAddress=device.deviceAddress;
        mP2pManger.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                System.out.println("连接"+device.deviceName+"成功");
            }

            @Override
            public void onFailure(int reason) {
                System.out.println("连接"+device.deviceName+"失败:"+reason);
            }
        });
    }

    @Override
    public void onGroupInfoAvailable(WifiP2pGroup group){

    }
}