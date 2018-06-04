package com.fastlib;

import android.view.View;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.app.task.InfiniteAction;
import com.fastlib.app.task.NoReturnAction;
import com.fastlib.app.task.Task;
import com.fastlib.app.task.TaskLauncher;
import com.fastlib.app.task.ThreadType;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    TaskLauncher mLauncher;

	@Override
	protected void alreadyPrepared(){
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                super.run();
                System.out.println("shutdown");
            }
        });
	}

	@Bind(R.id.bt)
	public void onBt(View view){

	}

	@Bind(R.id.bt2)
	public void onBt2(View view){

	}
}