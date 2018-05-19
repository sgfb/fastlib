package com.fastlib;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
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
import com.fastlib.db.DatabaseGetCallback;
import com.fastlib.db.FastDatabase;
import com.fastlib.net.NetManager;
import com.fastlib.net.Request;
import com.fastlib.net.listener.GlobalListener;
import com.fastlib.net.param_parse.JsonParamParser;
import com.fastlib.net.param_parse.MapParamParser;
import com.fastlib.net.param_parse.NetBeanWrapperParser;
import com.fastlib.net.param_parse.PrimitiveParamParser;
import com.fastlib.net.param_parse.SpinnerParamParser;
import com.fastlib.net.param_parse.TextViewParamParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{

    @Override
    protected void alreadyPrepared(){

    }

    @Bind(R.id.bt)
    private void commit(){
        WebViewActivity.start(this,"http://www.baidu.com");
    }

    @Bind(R.id.bt2)
	private void commit2(){

	}
}