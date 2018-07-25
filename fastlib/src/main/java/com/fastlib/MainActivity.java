package com.fastlib;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.annotation.Event;
import com.fastlib.annotation.LocalData;
import com.fastlib.app.EventObserver;
import com.fastlib.app.module.FastActivity;
import com.fastlib.db.FastDatabase;
import com.fastlib.net.Request;
import com.fastlib.net.listener.SimpleListener;
import com.fastlib.utils.ImageUtil;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{

	@Override
	public void alreadyPrepared(){

	}

	@Bind(R.id.bt)
	private void commit(View view){

	}

	@Bind(R.id.bt2)
	private void commit2(){

	}
}