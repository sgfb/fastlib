package com.fastlib.temp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by sgfb on 17/6/26.
 */
public abstract class BluetoothClientThread implements Runnable{
    private boolean isConnected=false;
    private BluetoothSocket mSocket;

    protected abstract void manageSocket(BluetoothSocket socket);

    public BluetoothClientThread(BluetoothDevice device){
        try {
            mSocket=device.createRfcommSocketToServiceRecord(UUID.fromString("550E8400-E29B-11D4-A716-446655440000"));
        } catch (IOException e){
            //do nothing
        }
    }

    @Override
    public void run() {
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        try {
            System.out.println("开始接收蓝牙连接");
            mSocket.connect();
            System.out.println("有蓝牙接入");
            isConnected=true;
        } catch (IOException e) {
            try {
                mSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
        manageSocket(mSocket);
    }

    public void close(){
        isConnected=false;
        try {
            mSocket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean isConnected(){
        return isConnected;
    }
}