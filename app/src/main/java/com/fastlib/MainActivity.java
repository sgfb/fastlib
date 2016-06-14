package com.fastlib;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fastlib.db.FastDatabase;
import com.fastlib.net.Listener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;
import com.fastlib.net.Result;
import com.fastlib.utils.ImageUtil;
import com.fastlib.widget.PercentView;
import com.fastlib.widget.RecycleListView;
import com.fastlib.widget.RoundImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 16/5/10.
 */
public class MainActivity extends AppCompatActivity{
    private static final String WX_URL="https://api.mch.weixin.qq.com/pay/unifiedorder";
    final String APPID="wxa316aa6fc6512be8";
    final String MCH_ID="1338296101";
    final String KEY="huayuzhishengchuanmei15058059534";

    class Link{
        public int id;
        public int type;
        public String image;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bt=(Button)findViewById(R.id.bt);
        Button pay=(Button)findViewById(R.id.pay);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File f=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"picture.png");
//                data.put("user_login", userName);
//                data.put("token", token);
//                data.put("content", content);
//                data.put("who_can_see", whoCanSee);
                NetQueue.getInstance()
                        .netRequest(new Request("http://www.bbang168.com/index.php?g=mobile&m=SNS&a=postMsg")
                                .put("user_login", "13353353534").put("token","f1f348ebf7e68970791edbf9578a2edcecf12de1735c6401").put("content", "other").put("who_can_see",3).put("photo0", f).setListener(new Listener() {
                                    @Override
                                    public void onResponseListener(Result result) {
                                        System.out.println("success:" + result.toString());
                                    }

                                    @Override
                                    public void onErrorListener(String error) {
                                        System.out.println("error:" + error);
                                    }
                                }));
            }
        });
//        bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v){
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            URL url=new URL(WX_URL);
//                            HttpURLConnection con= (HttpURLConnection) url.openConnection();
//                            con.setDoOutput(true);
//                            con.setDoInput(true);
//                            con.setRequestMethod("POST");
//                            OutputStream out=con.getOutputStream();
//                            String s=getXMLString();
//                            out.write(s.getBytes());
//                            InputStream in=con.getInputStream();
//                            ByteArrayOutputStream baos=new ByteArrayOutputStream();
//                            byte[] data=new byte[1024];
//                            int len;
//                            while((len=in.read(data))!=-1)
//                                baos.write(data,0,len);
//                            System.out.println(new String(baos.toByteArray()));
//                            out.close();
//                            in.close();
//                            con.disconnect();
//                        } catch (MalformedURLException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
//            }
//        });
//        pay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v){
//
//            }
//        });
    }

    public String getXMLString(){
        StringBuilder sb=new StringBuilder();
        sb.append("<xml>")
                .append("<appid>").append(APPID).append("</appid>")
                .append("<mch_id>").append(MCH_ID).append("</mch_id>")
                .append("<nonce_str>").append("5K8264ILTKCH16CQ2502SI8ZNMTM67VS").append("</nonce_str>")
                .append("<body>").append("test").append("</body>")
                .append("<out_trade_no>").append("201605172135").append("</out_trade_no>")
                .append("<total_fee>").append(1).append("</total_fee>")
                .append("<spbill_create_ip>").append("125.120.237.57").append("</spbill_create_ip>")
                .append("<trade_type>").append("APP").append("</trade_type>")
                .append("<notify_url>").append("http://www.baidu.com").append("</notify_url>")
                .append("<sign>").append("8AE53095DAD3C8E9B97E86B43D62BE2E").append("</sign>")
                .append("</xml>");
        return sb.toString();
    }
}