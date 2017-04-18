package com.fastlib.base;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Created by sgfb on 16/9/29.
 * 简易webview模块
 */
public abstract class AbsWebViewActivity extends AppCompatActivity {
    public static final String ARG_URL = "url";
    public static final String ARG_TITLE = "title";
    public static final String ARG_DATA = "data"; //本地html数据

    private WebView mWebView;
    private ProgressBar mProgress;
    private String mUrl;

    public abstract void webTitle(String title);

    protected void init(int webViewId, int progressId) throws UnsupportedEncodingException {
        mWebView = (WebView) findViewById(webViewId);
        mProgress = (ProgressBar) findViewById(progressId);
        mUrl = getIntent().getStringExtra(ARG_URL);
        String data = getIntent().getStringExtra(ARG_DATA);
        String title = getIntent().getStringExtra(ARG_TITLE);

        webTitle(title);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
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

    class ChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mProgress.setProgress(newProgress);
            if (newProgress >= 100)
                mProgress.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            webTitle(title);
            mProgress.setVisibility(View.VISIBLE);
            mProgress.setProgress(20);
        }
    }

    class WebClient extends WebViewClient {

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
    }
}
