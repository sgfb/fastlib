package com.fastlib;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.net.Request;
import com.fastlib.net.listener.SimpleListener;

import android.view.View;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{

	@Override
	protected void alreadyPrepared(){

	}

	@Bind(R.id.bt)
	public void onBt(View view){
		Request request=new Request("put","http://192.168.2.111:8080/FastProject/Test");
		request.setListener(new SimpleListener<String>(){

			@Override
			public void onResponseListener(Request r, String result) {
				System.out.println("result:"+result);
			}
		});
		request.start();
	}
}