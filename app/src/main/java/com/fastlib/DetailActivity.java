package com.fastlib;

import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.SurfaceView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.annotation.LocalData;
import com.fastlib.app.FastActivity;
import com.fastlib.utils.N;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by sgfb on 17/6/26.
 */
@ContentView(R.layout.act_detail)
public class DetailActivity extends FastActivity{
    public static final String ARG_BOOL_SERVER ="isServer";
    public static final String ARG_STR_HOST_NAME ="hostName";

    @Bind(R.id.surface)
    SurfaceView mSurfaceView;
    @Bind(R.id.filePath)
    EditText mFilePath;
    @Bind(R.id.speed)
    TextView mSpeed;
    @Bind(R.id.status)
    TextView mStatus;
    @Bind(R.id.downloadProgress)
    ProgressBar mProgressBar;

    @LocalData(ARG_STR_HOST_NAME)
    String mHost;
    @LocalData(ARG_BOOL_SERVER)
    boolean isServer;
    ServerSocket mServerSocket;
    Socket mSocket;
    DataInputStream mIn;
    DataOutputStream mOut;
    Camera mCamera;
    MediaPlayer mMediaPlayer;
    ArrayBlockingQueue<byte[]> mPhotoDatas=new ArrayBlockingQueue<>(1000);

    @Override
    protected void alreadyPrepared(){
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mStatus.setText("开启服务，等待客户端接入");
                        }
                    });
                    if(isServer){
                        mServerSocket=new ServerSocket(2888);
                        mSocket=mServerSocket.accept();
                    }
                    else {
                        mSocket=new Socket(mHost,2888);
                    }
                    mIn=new DataInputStream(mSocket.getInputStream());
                    mOut=new DataOutputStream(mSocket.getOutputStream());
                    monitoringInputStream();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mStatus.setText("客户端已接入,等待文件名传入");
                        }
                    });
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        mCamera=Camera.open();
        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                try {
                    mPhotoDatas.put(data);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Bind(R.id.sendFile)
    private void sendFile(){
        if(mSocket!=null){
            String path=mFilePath.getText().toString();
            if(TextUtils.isEmpty(path)){
                N.showShort(this,"路径不能为空");
                return;
            }
            final File file=new File(Environment.getExternalStorageDirectory(),path);
            if(file.exists()){
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mOut.writeUTF(file.getName());
                            mOut.writeLong(file.length());
                            InputStream fileIn=new FileInputStream(file);
                            byte[] buff=new byte[4096];
                            int len;
                            while((len=fileIn.read(buff))!=-1)
                                mOut.write(buff,0,len);
                            fileIn.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            else N.showLong(this,"文件不存在");
        }
        else N.showShort(this,"客户端还未连接");
    }

    private void setStatus(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatus.setText(message);
            }
        });
    }

    private void setSpeed(final long speed){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSpeed.setText(Formatter.formatFileSize(DetailActivity.this,speed));
            }
        });
    }

    private void setProgress(final long downloadedByte){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setProgress((int) (downloadedByte*100/mFileCount));
            }
        });
    }

    String mFileName;
    long mFileCount;

    private void monitoringInputStream(){
        mThreadPool.execute(new Runnable() {
            @Override
            public void run(){
//                try {
//                    while(true){
//
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }

    private File createNewFile() throws IOException {
        File file=new File(Environment.getExternalStorageDirectory(), mFileName);
        file.createNewFile();
        return file;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            if(mServerSocket!=null)
                mServerSocket.close();
            if(mSocket!=null)
                mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.setPreviewCallback(null);
        mCamera.unlock();
        mCamera.release();
    }
}
