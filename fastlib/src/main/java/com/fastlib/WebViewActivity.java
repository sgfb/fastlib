package com.fastlib;

import android.content.Context;
import android.content.Intent;

import com.fastlib.annotation.ContentView;
import com.fastlib.base.AbsWebViewActivity;

/**
 * Created by sgfb on 2018/5/16.
 */
@ContentView(R.layout.webview)
public class WebViewActivity extends AbsWebViewActivity{

    public static void start(Context context,String url){
        Intent intent=new Intent(context,WebViewActivity.class);
        intent.putExtra(ARG_STR_URL,url);
        intent.putExtra(ARG_INT_WEBVIEW_ID,R.id.webview);
        intent.putExtra(ARG_INT_PROGRESS_BAR_ID,R.id.progressbar);
        context.startActivity(intent);
    }

    @Override
    public void webTitle(String title) {

    }
}
