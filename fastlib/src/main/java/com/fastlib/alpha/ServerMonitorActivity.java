package com.fastlib.alpha;

import android.view.SurfaceView;

import com.fastlib.R;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.annotation.LocalData;
import com.fastlib.app.module.FastActivity;
import com.fastlib.app.task.EmptyAction;
import com.fastlib.app.task.Task;
import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.app.task.ThreadType;
import com.fastlib.alpha.VideoDecoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

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
    ServerSocket mServer;
    ArrayBlockingQueue<byte[]> mBuffer=new ArrayBlockingQueue<>(1024);
    ByteArrayOutputStream mBaos =new ByteArrayOutputStream();
    int mFrameLen=-1;

    @Override
    public void alreadyPrepared(){
        mDecoder=new VideoDecoder();
        try {
            mServer=new ServerSocket(mPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ThreadPoolManager.sSlowPool.execute(new Runnable() {
            @Override
            public void run() {
                while(!isFinishing()&&!isDestroyed()){
                    try {
                        mBaos.write(mBuffer.take());
                        System.out.println("buffer size:"+mBuffer.size());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    while((mBaos.size()>4&&mFrameLen==-1)||(mFrameLen>-1&&mBaos.size()>mFrameLen)){
                        //得到帧长
                        if(mBaos.size()>4&&mFrameLen==-1){
                            mFrameLen=byteToInt(mBaos.toByteArray());
                            byte[] remain=new byte[mBaos.size()-4];
                            System.arraycopy(mBaos.toByteArray(),4,remain,0,remain.length);
                            mBaos.reset();
                            try {
                                mBaos.write(remain);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        //如果缓存池内长度大于帧长则取出帧播放
                        if(mBaos.size()>=mFrameLen&&mFrameLen!=-1){
                            byte[] frameData=new byte[mFrameLen];
                            System.arraycopy(mBaos.toByteArray(),0,frameData,0,mFrameLen);
                            mDecoder.addData(frameData);

                            if(mBaos.size()>mFrameLen){
                                byte[] r=new byte[mBaos.size()-mFrameLen];
                                System.arraycopy(mBaos.toByteArray(),mFrameLen,r,0,r.length);
                                mBaos.reset();
                                try {
                                    mBaos.write(r);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else mBaos.reset();
                            mFrameLen=-1;
                        }
                    }
                }
            }
        });
    }

    private void listen(){
        ThreadPoolManager.sSlowPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket=mServer.accept();
                    InputStream in=socket.getInputStream();
                    while(!isFinishing()&&!isDestroyed()){
                        byte[] data=new byte[1024*10];
                        int len=in.read(data);
                        if(len==-1) break;
                        byte[] realData=new byte[len];
                        System.arraycopy(data,0,realData,0,len);
                        mBuffer.put(realData);
                    }
                    in.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private int byteToInt(byte[] data){
        int value=0;
        value|=((int)data[0]<<24&0xff000000);
        value|=((int)data[1]<<16&0xff0000);
        value|=((int)data[2]<<8&0xff00);
        value|=((int)data[3]&0xff);
        return value;
    }

    @Bind(R.id.start)
    private void start(){
        mDecoder.start(mSurfaceView.getHolder().getSurface());
        startTask(Task.begin(new EmptyAction() {
            @Override
            protected void executeAdapt() {
                listen();
            }
        },ThreadType.MAIN).setDelay(2500));
    }

    @Bind(R.id.play)
    private void play(){
        listen();
    }
}
