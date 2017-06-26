package com.fastlib.temp;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by sgfb on 17/6/26.
 */
public class BluetoothChatClient extends BluetoothClientThread{
    private InputStream mIn;
    private OutputStream mOut;
    private BluetoothChat.BluetoothInputListener mListener;

    public BluetoothChatClient(BluetoothDevice device) {
        super(device);
    }

    @Override
    protected void manageSocket(BluetoothSocket socket){
        try {
            mIn=socket.getInputStream();
            mOut=socket.getOutputStream();
            if(mListener!=null)
                mListener.dataResult(mIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(byte[] message){
        if(isConnected()){
            try {
                mOut.write(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(byte[] message,int start,int len){
        if(isConnected()){
            try {
                mOut.write(message,start,len);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public BluetoothChat.BluetoothInputListener getListener() {
        return mListener;
    }

    public void setListener(BluetoothChat.BluetoothInputListener listener) {
        mListener = listener;
        if(mListener!=null&&isConnected())
            mListener.dataResult(mIn);
    }
}
