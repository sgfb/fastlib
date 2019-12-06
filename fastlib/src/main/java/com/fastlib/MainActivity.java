package com.fastlib;

import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.net.Request;
import com.fastlib.net.listener.SimpleListener;
import com.fastlib.utils.N;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mStatus.setText("连接成功");
                    }
                });
            }
        });
    }

    @Bind(R.id.bt2)
    private void bt2() {
        if(mHttpCore==null){
            N.showLong(this,"未初始化连接");
            return;
        }
//        StringBuilder sb=new StringBuilder();
//        List<Pair<String,String>> list=new ArrayList<>();
//        list.add(Pair.create("param1","value1"));
//        list.add(Pair.create("param2","value2"));
//        loadParams(list,sb);
//        mHttpCore.addHeader("Content-Type","application/x-www-form-urlencoded");
//        mHttpCore.addPendingInputStream(new ByteArrayInputStream(sb.toString().getBytes()));
//        System.out.println("已写入到网络缓存");
        ThreadPoolManager.sSlowPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mHttpCore.begin();
                    System.out.println("发起请求成功");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Bind(R.id.bt3)
    private void closeSocket(){
        Request request=new Request("http://www.baidu.com","get");
        request.setListener(new SimpleListener<String>(){

            @Override
            public void onResponseListener(Request r, String result) {
                System.out.println(result);
            }
        });
        request.start();
//        if(mHttpCore==null||!mHttpCore.isConnected()){
//            N.showLong(this,"未连接");
//            return;
//        }
//        ThreadPoolManager.sSlowPool.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    mHttpCore.disconnect();
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mStatus.setText("已关闭");
//                        }
//                    });
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
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

    /**
     * 拼接字符串参数
     *
     * @param params
     * @param sb
     */
    private void loadParams(List<Pair<String, String>> params, StringBuilder sb) {
        if (params == null || params.size() <= 0)
            return;
        Iterator<Pair<String, String>> iter = params.iterator();

        while (iter.hasNext()) {
            Pair<String, String> pair = iter.next();
            try {
                sb.append(pair.first).append("=").append(TextUtils.isEmpty(pair.second)?"": URLEncoder.encode(pair.second,"UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        sb.deleteCharAt(sb.length() - 1);
    }
}