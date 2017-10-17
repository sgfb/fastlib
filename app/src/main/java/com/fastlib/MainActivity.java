package com.fastlib;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.http.SslError;
import android.os.PowerManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.net.Request;
import com.fastlib.net.SimpleListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by sgfb on 2017/9/21.
 */
@ContentView(R.layout.act_main2)
public class MainActivity extends FastActivity{
    @Bind(R.id.web)
    WebView mWeb;

    @Override
    protected void alreadyPrepared(){
        mWeb.setWebViewClient(new WebViewClient());
        mWeb.loadUrl("http://www.baidu.com");
    }

    @Bind(R.id.bt)
    private void commit(){

    }
}