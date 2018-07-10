package com.fastlib;

import android.widget.ImageView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.annotation.Event;
import com.fastlib.annotation.LocalData;
import com.fastlib.app.EventObserver;
import com.fastlib.app.FastActivity;
import com.fastlib.app.task.EmptyAction;
import com.fastlib.app.task.Task;
import com.fastlib.app.task.ThreadType;
import com.fastlib.net.Request;

import java.util.Random;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{

	@Override
	protected void alreadyPrepared() {

	}

	@Bind(R.id.bt)
	private void bt(){
		net(new Request("http://www.baidu.com"));
	}

	@Bind(R.id.bt2)
	private void bt2(){

	}
}