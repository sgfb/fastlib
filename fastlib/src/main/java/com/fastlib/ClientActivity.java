package com.fastlib;

import android.widget.Button;
import android.widget.EditText;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.app.task.ThreadPoolManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.Locale;

/**
 * Create by sgfb on 2019/06/12
 * E-Mail:602687446@qq.com
 */
@ContentView(R.layout.act_client)
public class ClientActivity extends FastActivity {
    @Bind(R.id.ip)
    EditText mIp;
    private Socket mClient;
    private Process mProcess;

    @Override
    public void alreadyPrepared(){

    }

    @Bind(R.id.bt)
    private void bt(){
        if(mClient==null){
            ThreadPoolManager.sSlowPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        mClient=new Socket(mIp.getText().toString(),2888);
                        OutputStream out=mClient.getOutputStream();
                        mProcess=Runtime.getRuntime().exec("logcat -v time");
                        BufferedReader logcatReader=new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
                        String line;

                        while((line=logcatReader.readLine())!=null)
                            out.write((line + "\n").getBytes());
                        mProcess.destroy();
                        mProcess=null;
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        mClient=null;
                    }
                }
            });
        }
    }

    @Bind(R.id.bt2)
    private void bt2(){
        System.out.println(ThreadPoolManager.sSlowPool);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mClient!=null) {
            try {
                mClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(mProcess!=null) mProcess.destroy();
    }
}
