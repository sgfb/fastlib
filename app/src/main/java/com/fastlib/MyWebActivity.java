package com.fastlib;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.fastlib.base.AbsWebViewActivity;
import com.fastlib.utils.N;

import java.io.UnsupportedEncodingException;

/**
 * Created by sgfb on 17/4/7.
 */
public class MyWebActivity extends AbsWebViewActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        try {
            init(R.id.webView,R.id.progress);
            WebView webView= (WebView) findViewById(R.id.webView);
            webView.addJavascriptInterface(MyWebActivity.this,"android");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void webTitle(String title) {

    }

    @JavascriptInterface
    public void test(){
        N.showShort(this,"调起本地函数");
    }
}
