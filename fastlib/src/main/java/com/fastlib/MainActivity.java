package com.fastlib;

import com.fastlib.R;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.app.task.NoReturnAction;
import com.fastlib.app.task.Task;
import com.fastlib.app.task.ThreadType;
import com.fastlib.net.Request;
import com.fastlib.net.listener.SimpleListener;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Environment;
import android.view.View;

import android.widget.Button;

import java.io.File;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{

	@Override
	protected void alreadyPrepared(){

	}

	@Bind(R.id.bt)
	public void onBt(View view){

	}

	@Bind(R.id.bt2)
	public void onBt2(View view){

	}
}