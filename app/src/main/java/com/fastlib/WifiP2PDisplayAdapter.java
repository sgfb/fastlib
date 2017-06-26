package com.fastlib;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;

import com.fastlib.adapter.FastAdapter;
import com.fastlib.base.OldViewHolder;

/**
 * Created by sgfb on 17/6/26.
 */
public class WifiP2PDisplayAdapter extends FastAdapter<WifiP2pDevice>{

    public WifiP2PDisplayAdapter(Context context) {
        super(context,R.layout.item_wifi_p2p);
    }

    @Override
    public void binding(int position, WifiP2pDevice data, OldViewHolder holder){
        String status;
        holder.setText(R.id.name,data.deviceName);
        holder.setText(R.id.address,data.deviceAddress);
        holder.setText(R.id.type,data.primaryDeviceType);
        holder.setText(R.id.type2,data.secondaryDeviceType);
        switch (data.status){
            case WifiP2pDevice.AVAILABLE:status="可连接";break;
            case WifiP2pDevice.CONNECTED:status="已连接";break;
            case WifiP2pDevice.INVITED:status="失效";break;
            case WifiP2pDevice.UNAVAILABLE:status="不可连接";break;
            case WifiP2pDevice.FAILED:status="失败";break;
            default:status="未知状态";
        }
        holder.setText(R.id.status,status);
    }
}
