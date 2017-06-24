package com.fastlib;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.fastlib.adapter.FastAdapter;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.EventObserver;
import com.fastlib.app.FastActivity;
import com.fastlib.base.OldViewHolder;
import com.fastlib.db.And;
import com.fastlib.db.Condition;
import com.fastlib.db.FastDatabase;
import com.fastlib.net.Request;
import com.fastlib.net.ResponseStatus;
import com.fastlib.net.SimpleListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 17/5/10.
 */
@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        EventObserver.build(this);
        super.onCreate(savedInstanceState);
    }
    @Bind(R.id.bt)
    private void commit(TextView v){
        Request request=Request.obtain("get","http://www.chexge.com/api/v1/car/check");
        request.put("templateName","外观检测");
        request.setListener(new SimpleListener<String>(){

            @Override
            public void onResponseListener(Request r, String result) {
                System.out.println("result:"+result);
            }
        });
        net(request);
    }

    @Bind(R.id.bt2)
    private void commit2(){
        Request request=Request.obtain("get","http://www.chexge.com/api/v1/users/technician?storeId=1&page=1&size=10");
        request.put("storeId",1);
        request.put("page",1);
        request.put("size",10);
        request.setListener(new SimpleListener<String>(){

            @Override
            public void onResponseListener(Request r, String result) {
                System.out.println("result:"+result);
            }
        });
        net(request);
    }

    @Bind(R.id.bt3)
    private void commit3(View view){
        Bean b=new Bean();
        Gson gson=new Gson();
        b.id=1;
        b.content="sgfb";
        b.url="http:www.baidu.com";
        System.out.println(gson.toJson(b,Bean.class));
    }

    @Override
    protected void alreadyPrepared(){

    }
}