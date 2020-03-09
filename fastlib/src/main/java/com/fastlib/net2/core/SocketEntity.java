package com.fastlib.net2.core;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.Proxy;
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
    private Proxy mProxy;

    public SocketEntity(String url,Socket socket) {
        this(url,socket,null);
    }

    public SocketEntity(String url,Socket socket,Proxy proxy){
        mUrl=url;
        mSocket = socket;
        mProxy=proxy;
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

    public Proxy getProxy(){
        return mProxy;
    }

    public void close() throws IOException {
        if(mOutputStream!=null) {
            mOutputStream.close();
            mOutputStream=null;
        }
        if(mInputStream!=null){
            mInputStream.close();
            mOutputStream=null;
        }
        mSocket.close();
        mSocket=null;
    }

    public boolean isValid(Proxy proxy) throws IOException {
        if(!mSocket.isClosed()&&!mSocket.isInputShutdown()&&!mSocket.isOutputShutdown()){
            InputStream inputStream=getInputStream();
            if(inputStream.available()<=0){
                int timeout=mSocket.getSoTimeout();
                mSocket.setSoTimeout(1);
                inputStream.mark(1);

                try{
                    int result=inputStream.read();
                    if(result==-1)
                        return false;
                }catch (InterruptedIOException e){
                    //不做任何事 异常的话说明这个socket是有效的
                }finally {
                    mSocket.setSoTimeout(timeout);
                }
            }
            return true;
        }
        return false;
    }
}
