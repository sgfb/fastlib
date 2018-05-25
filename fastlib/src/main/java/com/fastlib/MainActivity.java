package com.fastlib;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.utils.ScreenUtils;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{

	@Override
	protected void alreadyPrepared(){
	}

	@Bind(value = R.id.bt)
	public void onBt(View view){

	}

	@Bind(value = R.id.bt2,runOnWorkThread = true)
	private void onBt2(View v){

	}
}