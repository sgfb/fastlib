package com.fastlib;

import com.fastlib.R;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.base.AbsWebViewActivity;

import android.content.Intent;
import android.view.View;

import android.widget.ImageView;
import android.widget.Button;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity {

	@Bind(R.id.bt)
	public void onBt(View view){
		Intent intent=new Intent(this,WebViewActivity.class);
		intent.putExtra(WebViewActivity.ARG_STR_URL,"https://m.mafengwo.cn/movie/detail/442273.html");
		intent.putExtra(WebViewActivity.ARG_INT_WEBVIEW_ID,R.id.webView);
		startActivity(intent);
	}

	@Override
	public void alreadyPrepared() {

	}
}