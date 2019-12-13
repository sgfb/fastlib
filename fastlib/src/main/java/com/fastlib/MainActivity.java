package com.fastlib;

import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.db.SaveUtil;
import com.fastlib.net.Request;
import com.fastlib.net.listener.SimpleListener;
import com.fastlib.utils.N;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;

    @Bind(R.id.bt)
    private void bt() {
        final String address = mSendData.getText().toString().trim();

        if (TextUtils.isEmpty(address)) {
            N.showLong(this, "地址不能为空");
            return;
        }
        mStatus.setText("开始连接");
        ThreadPoolManager.sSlowPool.execute(new Runnable() {
            @Override
            public void run() {
                final long timer = System.currentTimeMillis();
                if (mHttpCore == null)
                    mHttpCore = new SimpleHttpCoreImpl(address);
                try {
                    mHttpCore.begin();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    InputStream inputStream = mHttpCore.getInputStream();
                    byte[] buffer = new byte[4096];
                    int len;

                    while ((len = inputStream.read(buffer)) != -1)
                        baos.write(buffer, 0, len);
                    mHttpCore.end();
                    HttpTimer httpTimer = mHttpCore.getHttpTimer();
                    System.out.println(String.format(Locale.getDefault(), "consume init:%s,connection:%s,ttfb:%s,download:%s",
                            httpTimer.getInitConsume(), httpTimer.getConnectionConsume(), httpTimer.getTTFB(), httpTimer.getDownloadConsume()));
                    System.out.println(baos);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("consume:" + (System.currentTimeMillis() - timer));
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
        mTabLayout.getTabAt(0).setText("changed");
//        if (mHttpCore == null) {
//            N.showLong(this, "未初始化连接");
//            return;
//        }
//        StringBuilder sb=new StringBuilder();
//        List<Pair<String,String>> list=new ArrayList<>();
//        list.add(Pair.create("param1","value1"));
//        list.add(Pair.create("param2","value2"));
//        loadParams(list,sb);
//        mHttpCore.addHeader("Content-Type","application/x-www-form-urlencoded");
//        mHttpCore.addPendingInputStream(new ByteArrayInputStream(sb.toString().getBytes()));
//        System.out.println("已写入到网络缓存");
//        ThreadPoolManager.sSlowPool.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    mHttpCore.begin();
//                    System.out.println("发起请求成功");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    @Bind(R.id.bt3)
    private void closeSocket() {
        final long timer = System.currentTimeMillis();
        Request request = new Request(mSendData.getText().toString().trim(), "get");
        request.setListener(new SimpleListener<String>() {

            @Override
            public void onResponseListener(Request r, String result) {
                System.out.println("consume:" + (System.currentTimeMillis() - timer));
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
    private void receiveData() {
        if (mHttpCore == null || !mHttpCore.isConnected()) {
            N.showLong(this, "未连接");
            return;
        }
        ThreadPoolManager.sSlowPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream in = mHttpCore.getInputStream();
                    byte[] buffer = new byte[4096];
                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int len;
                    while ((len = in.read(buffer)) != -1)
                        baos.write(buffer, 0, len);
                    mHttpCore.end();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String result = baos.toString();
                            System.out.println("result length:" + result.length());
                            mResponseData.setText(result);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void alreadyPrepared() {
        mTabLayout.addTab(mTabLayout.newTab().setText("tab1"));
        mTabLayout.addTab(mTabLayout.newTab().setText("tab2"));
        mTabLayout.addTab(mTabLayout.newTab().setText("tab3"));
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
                sb.append(pair.first).append("=").append(TextUtils.isEmpty(pair.second) ? "" : URLEncoder.encode(pair.second, "UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        sb.deleteCharAt(sb.length() - 1);
    }
}