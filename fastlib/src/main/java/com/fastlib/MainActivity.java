package com.fastlib;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.app.task.Action;
import com.fastlib.app.task.NetAction;
import com.fastlib.app.task.NoReturnAction;
import com.fastlib.app.task.Task;
import com.fastlib.app.task.TaskLauncher;
import com.fastlib.net.NetManager;
import com.fastlib.net.Request;
import com.fastlib.net.param_parse.MapParamParser;
import com.fastlib.net.param_parse.NetBeanWrapperParser;
import com.fastlib.net.param_parse.PrimitiveParamParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
	Request request;

    @Override
    protected void alreadyPrepared(){
		request=new Request("http://www.baidu.com");
		request.getParamParserManager().addParser(new PrimitiveParamParser());
		request.getParamParserManager().addParser(new MapParamParser());
		request.getParamParserManager().addParser(new NetBeanWrapperParser());
    }

    @Bind(R.id.bt)
    private void commit(){
		Map<String,String> map=new HashMap<>();
		map.put("param1","name");
		map.put("param2","password");
		request.put(map);
		System.out.println(request);
    }

    @Bind(R.id.bt2)
	private void commit2(){
		TestBean tb=new TestBean();
		tb.id=1;
		tb.name="sgfb";
		request.put(tb);
		System.out.println(request);
	}
}