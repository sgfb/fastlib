package com.fastlib;

import android.content.Context;
import android.content.Intent;

import com.fastlib.annotation.ContentView;
import com.fastlib.base.AbsWebViewActivity;

@ContentView(R.layout.act_web)
public class WebActivity extends AbsWebViewActivity{

    public static void launcher(Context context,String url){
        Intent intent=new Intent(context,WebActivity.class);
        intent.putExtra(ARG_STR_URL,url);
        intent.putExtra(ARG_INT_PROGRESS_BAR_ID,R.id.progressBar);
        intent.putExtra(ARG_INT_WEBVIEW_ID,R.id.webView);
        context.startActivity(intent);
    }

    @Override
    public void webTitle(String title) {

    }
}
