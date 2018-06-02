package com.fastlib;

import com.fastlib.R;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.app.task.NoReturnAction;
import com.fastlib.app.task.Task;
import com.fastlib.app.task.ThreadType;

import android.content.Intent;
import android.view.View;

import android.widget.Button;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{

	@Override
	protected void alreadyPrepared(){

	}

	@Bind(R.id.bt)
	public void onBt(View view){
		startTask(Task.begin("10")
				.infinite(new NoReturnAction<String>() {
					@Override
					public void executeAdapt(String param) {
						System.out.println(param);
					}
				}, ThreadType.WORK));
	}

	@Bind(R.id.bt2)
	public void onBt2(View view){
		
	}
}