package com.fastlib.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.fastlib.R;


/**
 * Created by sgfb on 16/9/29.
 * 简易webview模块
 */
public abstract class AbsWebViewActivity extends AppCompatActivity{
    public static final String ARG_URL="url";
    public static final String ARG_TITLE="title";
    public static final String ARG_DATA="data"; //本地html数据

    private WebView mWebView;
    private ProgressBar mProgress;
    private String mUrl;

    public abstract void webTitle(String title);

    protected void init(int webViewId,int progressId){
        mWebView= (WebView) findViewById(webViewId);
        mProgress = (ProgressBar) findViewById(progressId);
        mUrl=getIntent().getStringExtra(ARG_URL);
        String data=getIntent().getStringExtra(ARG_DATA);
        String title=getIntent().getStringExtra(ARG_TITLE);

        webTitle(title);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new ChromeClient());
        if(TextUtils.isEmpty(data))
            mWebView.loadUrl(mUrl);
        else
            mWebView.loadData(data,"text/html","UTF-8");
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK&&mWebView.canGoBack()){
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    class ChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress){
            mProgress.setProgress(newProgress);
            if(newProgress>=100)
                mProgress.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedTitle(WebView view, String title){
            webTitle(title);
            mProgress.setVisibility(View.VISIBLE);
            mProgress.setProgress(20);
        }
    }
}
