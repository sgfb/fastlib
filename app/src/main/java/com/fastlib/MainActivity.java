package com.fastlib;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fastlib.db.FastDatabase;
import com.fastlib.interf.AdapterViewState;
import com.fastlib.net.Request;
import com.fastlib.test.StateListView;
import com.fastlib.test.TestAdapter;

public class MainActivity extends AppCompatActivity{
    private final int DATABASE_VERSION=3;
    private boolean isAsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FastDatabase.getInstance().getConfig().setVersion(DATABASE_VERSION);
        Button bt=(Button)findViewById(R.id.bt);
        StateListView lv=(StateListView)findViewById(R.id.list);
        Request request=new Request("post","http://192.168.1.112:8080/FastProject/Login");
        request.put("limit", "5");
        final TestAdapter adapter=new TestAdapter(this,request,R.layout.test_adapter);
        adapter.setLoadCount(5);
        lv.setAdapter(adapter);
        TextView tv=new TextView(this);
        tv.setText("没有更多数据");
        TextView tv2=new TextView(this);
        tv2.setText("加载中。。。");
        lv.addStateView(AdapterViewState.STATE_NO_MORE, tv, AdapterViewState.location_foot);
        lv.addStateView(AdapterViewState.STATE_LOADING,tv2,AdapterViewState.location_foot);
        adapter.setViewStateListener(lv);
        bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                adapter.refresh();
            }
        });
    }
}