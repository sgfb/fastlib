package com.fastlib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.fastlib.annotation.Bind;
import com.fastlib.app.FastActivity;
import com.fastlib.db.SaveUtil;
import com.fastlib.db.ServerCache;
import com.fastlib.net.Listener;
import com.fastlib.net.Request;
import com.fastlib.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * Created by sgfb on 16/12/29.
 */
public class MainActivity extends FastActivity {
    @Bind(R.id.image)
    ImageView iv;
    ServerCache mServerCache;
    Request mRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        mRequest=new Request("http://192.168.131.125:8084/FastProject/Test")
                .put("image",new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"test.png"))
                .put("user_login","13353353534")
                .put("token","sdfoijasofojdsfoiasjfiosjdfoijasdfjioijof")
                .put("content","long long long long long long long long long long long long long long long long long long long long long long long long " +
                        "oaidsjfoasjdfoijsdfoijaosdjfojsdofijoi噢嘶佛山大家佛啊扫地机佛教啊送大家佛撒娇的佛教萨当年佛啊诶你佛 i 啊睡觉哦放 i 家 text")
                .setListener(new Listener<List<Bean>>(){
                    @Override
                    public void onResponseListener(Request r,List<Bean> result){
                        for(Bean b:result)
                            System.out.println(b);
                    }
                    @Override
                    public void onErrorListener(Request r, String error) {
                        System.out.println("error:"+error);
                    }
        });
        mRequest.setSendGzip(true);
        mRequest.setReceiveGzip(true);
//        mServerCache=new ServerCache(request,"testCache",null,mThreadPool);
//        mServerCache.setCacheTimeLife(1000*10);
    }

    @Bind(R.id.bt)
    public void commit1(View v){
        net(mRequest);
//        mServerCache.refresh(false);
    }

    @Bind(R.id.bt2)
    public void commit2(View v){
        ServerCache sc=new ServerCache(new Request(""),"testCache",null,mThreadPool);
    }

    public class Bean{
        public int id;
        public String name;

        @Override
        public String toString() {
            return "id:"+id+" name:"+name;
        }
    }
}