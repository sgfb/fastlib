package com.fastlib;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.utils.N;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;
import java.util.Map;


@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity {
    @Bind(R.id.status)
    TextView mStatus;
    @Bind(R.id.responseData)
    TextView mResponseData;
    @Bind(R.id.sendData)
    EditText mSendData;
    SimpleHttpCoreImpl mHttpCore;

    @Bind(R.id.bt)
    private void bt() {
        final String address=mSendData.getText().toString().trim();

        if(TextUtils.isEmpty(address)){
            N.showLong(this,"地址不能为空");
            return;
        }
        mStatus.setText("开始连接");
        ThreadPoolManager.sSlowPool.execute(new Runnable() {
            @Override
            public void run() {
                mHttpCore=new SimpleHttpCoreImpl(address);
                try {
                    mHttpCore.connect();
                    ResponseHeader header=mHttpCore.getResponseHeader();
                    System.out.println(String.format(Locale.getDefault(),"protocol:%s code:%d message:%s",header.getProtocol(),header.getCode(),header.getMessage()));
                    for(Map.Entry<String,List<String>> entry:header.getHeader().entrySet()){
                        System.out.println("header:"+entry.getKey()+","+entry.getValue().get(0));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mStatus.setText("连接成功");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mStatus.setText("连接失败");
                        }
                    });
                }
            }
        });
    }

    @Bind(R.id.bt2)
    private void bt2() {
        if(mHttpCore==null||!mHttpCore.isConnected()){
            N.showLong(this,"未连接");
            return;
        }
        ThreadPoolManager.sSlowPool.execute(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Bind(R.id.bt3)
    private void closeSocket(){
        if(mHttpCore==null||!mHttpCore.isConnected()){
            N.showLong(this,"未连接");
            return;
        }
        ThreadPoolManager.sSlowPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mHttpCore.disconnect();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mStatus.setText("已关闭");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Bind(R.id.bt4)
    private void receiveData(){
        if(mHttpCore==null||!mHttpCore.isConnected()){
            N.showLong(this,"未连接");
            return;
        }
        ThreadPoolManager.sSlowPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream in=mHttpCore.getInputStream();
                    byte[] buffer=new byte[4096];
                    final ByteArrayOutputStream baos=new ByteArrayOutputStream();
                    int len;
                    while((len=in.read(buffer))!=-1)
                        baos.write(buffer,0,len);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mResponseData.setText(new String(baos.toByteArray()));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void alreadyPrepared(){

    }
}