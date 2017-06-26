package com.fastlib.temp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by sgfb on 17/6/26.
 */
public abstract class BluetoothServerThread implements Runnable{
    private boolean isConnected;
    private BluetoothServerSocket mServerSocket;

    protected abstract void manageSocket(BluetoothSocket socket);

    public BluetoothServerThread(){
        BluetoothAdapter ba=BluetoothAdapter.getDefaultAdapter();
        try {
            mServerSocket=ba.listenUsingRfcommWithServiceRecord("fastlib", UUID.fromString("550E8400-E29B-11D4-A716-446655440000"));
        } catch (IOException e) {
            //do nothing
        }
    }

    @Override
    public void run(){
        BluetoothSocket socket=null;
        while(true){
            try{
                System.out.println("等待客户端接入");
                socket=mServerSocket.accept();
                System.out.println("已有客户端接入");
                isConnected=true;
            } catch (IOException e) {
                break; //do nothing just get out
            }
            if(socket!=null){
                try {
                    mServerSocket.close();
                } catch (IOException e) {
                    //do nothing
                }
                manageSocket(socket);
                break;
            }
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void disconnect(){
        isConnected=false;
        try {
            mServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}