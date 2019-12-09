package com.fastlib;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by sgfb on 2019/12/9
 * E-mail:602687446@qq.com
 */
public class SocketEntity{
    private String mUrl;
    private Socket mSocket;
    private InputStream mInputStream;
    private OutputStream mOutputStream;

    public SocketEntity(String url,Socket socket) {
        mUrl=url;
        mSocket = socket;
    }

    public String getUrl(){
        return mUrl;
    }

    public @NonNull Socket getSocket(){
        return mSocket;
    }

    public InputStream getInputStream() throws IOException {
        if(mInputStream==null)
            mInputStream=mSocket.getInputStream();
        return mInputStream;
    }

    public OutputStream getOutputStream() throws IOException {
        if(mOutputStream==null)
            mOutputStream=mSocket.getOutputStream();
        return mOutputStream;
    }

    public void close() throws IOException {
        if(mInputStream!=null)
            mInputStream.close();
        if(mOutputStream!=null)
            mOutputStream.close();
        mSocket.close();
    }

    public boolean isValid() throws IOException {
        return !mSocket.isClosed()&&mInputStream.available()!=-1;
    }
}
