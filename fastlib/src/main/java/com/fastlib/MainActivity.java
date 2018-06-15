package com.fastlib;

import android.view.View;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.app.task.TaskLauncher;

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