package com.fastlib;

import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.net.listener.SimpleListener;
import com.fastlib.net2.HttpProcessor;
import com.fastlib.net2.HttpTimer;
import com.fastlib.net2.Listener;
import com.fastlib.net2.Request;
import com.fastlib.net2.SimpleHttpCoreImpl;
import com.fastlib.utils.N;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


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
        Request request=new Request("http://www.sgfb.top:8082/getSimpleData");
        request.setListener(new Listener() {
            @Override
            public void onResponseSuccess(Request request, byte[] data) {
                System.out.println(new String(data));
            }
        });
        ThreadPoolManager.sSlowPool.execute(new HttpProcessor(request));
    }

    @Bind(R.id.bt2)
    private void bt2() {

    }

    @Bind(R.id.bt3)
    private void closeSocket() {

    }

    @Bind(R.id.bt4)
    private void receiveData() {

    }

    @Override
    public void alreadyPrepared() {

    }
}