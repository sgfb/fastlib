package com.fastlib;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.fastlib.base.AbsWebViewActivity;
import com.fastlib.utils.N;

import java.io.UnsupportedEncodingException;

/**
 * Created by sgfb on 18/3/9.
 */

public class WebActivity extends AbsWebViewActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_web);
        try {
            init(R.id.web,R.id.progressbar);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(this,"android");
    }

    @Override
    public void webTitle(String title) {

    }

    @JavascriptInterface
    public void justToast(String value){
        N.showShort(this,"from js:"+value);
    }
}
