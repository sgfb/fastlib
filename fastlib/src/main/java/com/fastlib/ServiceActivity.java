package com.fastlib;

import android.support.v4.widget.NestedScrollView;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Create by sgfb on 2019/06/12
 * E-Mail:602687446@qq.com
 */
@ContentView(R.layout.act_service)
public class ServiceActivity extends FastActivity {
    @Bind(R.id.scrollView)
    NestedScrollView mScrollView;
    @Bind(R.id.logcat)
    TextView mLogcat;
    ServerSocket mServer;

    @Override
    public void alreadyPrepared(){
        mLogcat.setMovementMethod(ScrollingMovementMethod.getInstance());
        ThreadPoolManager.sQuickPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mServer=new ServerSocket(2888);
                    Socket client=mServer.accept();
                    readLogcat(client);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void readLogcat(Socket client) throws IOException {
        BufferedReader reader=new BufferedReader(new InputStreamReader(client.getInputStream()));
        String line;
        while((line=reader.readLine())!=null){
            final String fLine=line;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int errorLevel=0;
                    String[] segment=fLine.split(" ");
                    if(segment.length>=3){
                        if(segment[2].toUpperCase().startsWith("W"))
                            errorLevel=1;
                        else if(segment[2].toUpperCase().startsWith("E")||segment[2].toUpperCase().startsWith("F"))
                            errorLevel=2;
                    }

                    String realLine=fLine+"\n";
                    int color=R.color.grey_900;
                    if(errorLevel==1)
                        color=R.color.yellow_800;
                    else if(errorLevel==2)
                        color=R.color.red_800;
                    mLogcat.append(Utils.getTextSomeOtherColor(0,realLine.length(),realLine,getResources().getColor(color)));
                    mScrollView.scrollBy(0,100);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mServer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
