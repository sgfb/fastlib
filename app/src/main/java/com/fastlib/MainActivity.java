package com.fastlib;

import android.widget.*;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;

@ContentView(R.layout.act_main2)
public class MainActivity extends FastActivity{
	@Bind(R.id.path)
	EditText mPath;
	@Bind(R.id.bt)
	Button mBt;
	@Bind(R.id.bt2)
	Button mBt2;
	@Bind(R.id.bt3)
	Button mBt3;
	@Bind(R.id.delete)
	Button mDelete;
	@Bind(R.id.gridView)
	GridView mGridView;

	@Override
	protected void alreadyPrepared(){

	}
}