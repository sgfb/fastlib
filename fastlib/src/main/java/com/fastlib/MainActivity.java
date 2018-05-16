package com.fastlib;

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
        NetManager.getInstance().setGlobalListener(new GlobalListener(){
            @Override
            public byte[] onRawData(Request r, byte[] data) {
                System.out.println("global raw:"+data.length);
                return super.onRawData(r, data);
            }

            @Override
            public String onTranslateJson(Request r, String json) {
                System.out.println("global json:"+json);
                return super.onTranslateJson(r, json);
            }

            @Override
            public Object onResponseListener(Request r, Object result, Object result2) {
                System.out.println("global response:"+result);
                return super.onResponseListener(r, result, result2);
            }

            @Override
            public String onErrorListener(Request r, String error) {
                System.out.println("global error:"+error);
                return super.onErrorListener(r, error);
            }
        });
    }

    @Bind(R.id.bt)
    private void commit(){
        startTask(Task.begin(new Request("http://www.baidu.com"))
        .next(new NetAction<String,String>(){

            @Override
            protected String executeAdapt(String r, Request request){
                System.out.println("result:"+r);
                return r;
            }
        }));
    }

    @Bind(R.id.bt2)
	private void commit2(){

	}
}