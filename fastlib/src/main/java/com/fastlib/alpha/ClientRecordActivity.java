package com.fastlib.alpha;

import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;

import com.fastlib.R;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.annotation.LocalData;
import com.fastlib.app.module.FastActivity;
import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.alpha.VideoEncoder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

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
    Socket mSocket;
    BlockingQueue<byte[]> mBuffer=new ArrayBlockingQueue<>(1024);

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
            mEncoder.start();
            ThreadPoolManager.sSlowPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        while(!isFinishing()&&!isDestroyed()){
                            byte[] data=mEncoder.pollFrameFromEncoder();
                            if(data==null) continue;
                            mBuffer.put(data);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Bind(R.id.reConnect)
    private void connect(){
        ThreadPoolManager.sSlowPool.execute(new Runnable(){
            @Override
            public void run() {
                try {
                    mSocket=new Socket(mAddress,mPort);
                    OutputStream out=mSocket.getOutputStream();
                    while(!mSocket.isClosed()&&!isDestroyed()&&!isFinishing()){
                        byte[] data=mBuffer.take();

                        System.out.println(mBuffer.size());
                        byte[] intByte=new byte[4];
                        intByte[0]= (byte) (data.length>>24);
                        intByte[1]= (byte) (data.length>>16);
                        intByte[2]= (byte) (data.length>>8);
                        intByte[3]= (byte) (data.length);
                        out.write(intByte);
                        out.write(data);
                    }
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
