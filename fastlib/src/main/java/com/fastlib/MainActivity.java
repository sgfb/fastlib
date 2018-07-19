package com.fastlib;

import android.os.Bundle;
import android.view.View;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.db.FastDatabase;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{

	@Override
	public void alreadyPrepared(){

	}

	@Bind(R.id.bt)
	private void commit(){
		Student student=new Student();
		student.id=1;
		student.a="bao";
		FastDatabase.getDefaultInstance(MainActivity.this).saveOrUpdateAsync(student,null);
	}

	@Bind(R.id.bt2)
	private void commit2(){
		Student student=FastDatabase.getDefaultInstance(this).getFirst(Student.class);
		System.out.println(student);
	}
}