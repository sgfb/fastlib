package com.fastlib;

import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
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
import java.net.InetAddress;
import java.net.Socket;

/**
 * Create by sgfb on 2019/03/19
 * E-Mail:602687446@qq.com
 */
@ContentView(R.layout.act_client_record)
public class ClientRecordActivity extends FastActivity{
    public static final String ARG_STR_ADDRESS="address";
    public static final String ARG_INT_PORT="port";

    @LocalData(ARG_INT_PORT)
    int mPort;
    @LocalData(ARG_STR_ADDRESS)
    String mAddress;
    VideoEncoder mEncoder;
    MediaProjectionManager mProjectionManager;
    MediaProjection mMediaProject;
    long mTimer;
    long mSendCount;

    @Override
    public void alreadyPrepared(){
        mEncoder=new VideoEncoder();
        mProjectionManager= (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(),1);
        connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK){
            mMediaProject=mProjectionManager.getMediaProjection(resultCode,data);
            mMediaProject.createVirtualDisplay("screenRecode",480,800,260,0,mEncoder.getSurface(),null,null);
        }
    }

    @Bind(R.id.reConnect)
    private void connect(){
        ThreadPoolManager.sSlowPool.execute(new Runnable(){
            @Override
            public void run() {
                try{
                    DatagramSocket dataSocket=new DatagramSocket(0);
                    while(!isDestroyed()&&!isFinishing()){
                        byte[] data=mEncoder.pollFrameFromEncoder();
                        if(data==null) continue;
                        mSendCount+=data.length;
                        if(System.currentTimeMillis()-mTimer>1000){
                            System.out.println("发送:"+mSendCount);
                            mTimer=System.currentTimeMillis();
                            mSendCount=0;
                        }
                        DatagramPacket pck=new DatagramPacket(data,data.length,InetAddress.getByName(mAddress),mPort);
                        dataSocket.send(pck);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
    }
}
