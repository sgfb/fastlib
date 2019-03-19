package com.fastlib;

import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.annotation.LocalData;
import com.fastlib.app.module.FastActivity;
import com.fastlib.app.task.ThreadPoolManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Create by sgfb on 2019/03/19
 * E-Mail:602687446@qq.com
 */
@ContentView(R.layout.act_server_monitor)
public class ServerMonitorActivity extends FastActivity{
    public static final String ARG_INT_PORT="port";

    @LocalData(ARG_INT_PORT)
    int mPort;
    @Bind(R.id.surfaceView)
    SurfaceView mSurfaceView;
    VideoDecoder mDecoder;
    long mTimer;
    long mReceiveCount=0;

    @Override
    public void alreadyPrepared(){
        mPort=2888;

        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mDecoder=new VideoDecoder(holder.getSurface());
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                System.out.println("surfaceChanged");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        listen();
    }

    private void listen(){
        ThreadPoolManager.sSlowPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket socket=new DatagramSocket(2888);
                    while(!isFinishing()&&!isDestroyed()){
                        byte[] data=new byte[1024*200];
                        DatagramPacket response=new DatagramPacket(data,data.length);
                        socket.receive(response);
                        byte[] realData=new byte[response.getLength()];
                        System.arraycopy(data,0,realData,0,realData.length);
                        mReceiveCount+=realData.length;
                        if(System.currentTimeMillis()-mTimer>1000){
                            System.out.println("接收视频信息："+mReceiveCount);
                            mReceiveCount=0;
                            mTimer=System.currentTimeMillis();
                        }
                        mDecoder.addData(realData);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Bind(R.id.start)
    private void start(){
        mDecoder.start();
    }
}
