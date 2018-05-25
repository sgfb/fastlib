package com.fastlib.base;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.fastlib.annotation.LocalData;
import com.fastlib.app.FastActivity;

import java.io.UnsupportedEncodingException;

/**
 * Created by sgfb on 16/9/29.
 * 简易webview模块
 */
public abstract class AbsWebViewActivity extends FastActivity{
    public static final String TAG=AbsWebViewActivity.class.getName();
    public static final String ARG_STR_URL = "URL";
    public static final String ARG_STR_TITLE = "title";
    public static final String ARG_STR_DATA = "data"; //本地html数据
    public static final String ARG_INT_WEBVIEW_ID="webviewId";
    public static final String ARG_INT_PROGRESS_BAR_ID="progressBarId";

    protected WebView mWebView;
    protected ProgressBar mProgress;
    protected String mUrl;

    public abstract void webTitle(String title);

    @Override
    protected void alreadyPrepared(){
        mWebView= (WebView) findViewById(getIntent().getIntExtra(ARG_INT_WEBVIEW_ID,0));
        mProgress= (ProgressBar) findViewById(getIntent().getIntExtra(ARG_INT_PROGRESS_BAR_ID,0));
        mUrl = getIntent().getStringExtra(ARG_STR_URL);
        String data = getIntent().getStringExtra(ARG_STR_DATA);
        String title = getIntent().getStringExtra(ARG_STR_TITLE);

        webTitle(title);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebClient());
        mWebView.setWebChromeClient(new ChromeClient());
        if (TextUtils.isEmpty(data)) mWebView.loadUrl(mUrl);
        else mWebView.loadData(data, "text/html;charset=UTF-8", null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected class ChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if(mProgress!=null){
                mProgress.setProgress(newProgress);
                if (newProgress >= 100)
                    mProgress.setVisibility(View.GONE);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            webTitle(title);
            if(mProgress!=null){
                mProgress.setVisibility(View.VISIBLE);
                mProgress.setProgress(20);
            }
        }
    }

    protected class WebClient extends WebViewClient{

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            if(url.startsWith("tel:")){
                Intent intent=new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            }
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon){
            Log.d(TAG,"page start:"+url);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url){
            Log.d(TAG,"page end:"+url);
            super.onPageFinished(view, url);
        }
    }
}
