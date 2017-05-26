package com.fastlib;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.ListView;

import com.fastlib.adapter.SingleAdapter;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.EventObserver;
import com.fastlib.app.FastActivity;
import com.fastlib.base.OldViewHolder;
import com.fastlib.net.Request;
import com.fastlib.net.SimpleListener;
import com.fastlib.net.SimpleListener2;
import com.fastlib.net.SimpleListener3;
import com.fastlib.test.jsonAdvanced.Bean;

import java.util.List;

/**
 * Created by sgfb on 17/5/10.
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends FastActivity{
    @Bind(R.id.list)
    ListView mList;
    MyAdapter mAdapter;
    boolean suspend=false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        EventObserver.build(this);
        super.onCreate(savedInstanceState);
    }

    @Bind(R.id.bt)
    private void commit(View v){
        net(Request.obtain("http://192.168.131.125:8084/FastProject/Test").setListener(new SimpleListener<byte[]>(){

            @Override
            public void onResponseListener(Request r,byte[] result){
                System.out.println("result:"+(result!=null?result.length:"result is null"));
            }
        }));
    }

    @Bind(R.id.bt2)
    private void commit2(){
        net(Request.obtain("http://192.168.131.125:8084/FastProject/Test").setListener(new SimpleListener(){

            @Override
            public void onResponseListener(Request r,Object result){
                System.out.println("result:"+result);
            }
        }));
//        mAdapter.getItemGroupByType(1).remove(0);
    }

    @Bind(R.id.bt3)
    private void commit3(View view){
//        if(suspend){
//            mAdapter.getItemGroupByType(1).setIsSuspend(suspend=false);
//        }
//        else{
//            mAdapter.getItemGroupByType(1).setIsSuspend(suspend=true);
//        }
    }

    @Override
    protected void alreadyPrepared(){

    }
}