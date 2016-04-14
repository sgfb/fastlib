package com.fastlib;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fastlib.adapter.BindingAdapter;
import com.fastlib.annotation.DatabaseInject;
import com.fastlib.app.EventObserver;
import com.fastlib.base.OldViewHolder;
import com.fastlib.db.FastDatabase;
import com.fastlib.interf.AdapterViewState;
import com.fastlib.net.Listener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;
import com.fastlib.net.Result;
import com.fastlib.utils.ImageUtil;
import com.fastlib.utils.N;
import com.fastlib.widget.Indicator;
import com.fastlib.widget.StateListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StateListView list=(StateListView)findViewById(R.id.list);
        Request request=new Request("post","http://192.168.1.110:8080/FastProject/Test");
        final SwipeRefreshLayout refresh=(SwipeRefreshLayout)findViewById(R.id.refresh);
        request.put("page", "0");
        final MyAdapter adapter=new MyAdapter(this,request);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.refresh();
                refresh.setRefreshing(false);
            }
        });
        adapter.setLoadCount(5);
        list.setAdapter(adapter);
    }

    class MyAdapter extends BindingAdapter<Bean>{

        public MyAdapter(Context context, Request request) {
            super(context,request,R.layout.test_adapter,true);
        }

        @Override
        public void binding(int position, Bean data, OldViewHolder holder) {
            holder.setText(R.id.textView,data.text);
        }

        @Override
        public List<Bean> translate(Result result){
            Gson gson=new Gson();
            Type type=new TypeToken<List<Bean>>(){}.getType();
            return  gson.fromJson(result.getBody(),type);
        }

        @Override
        public void getMoreDataRequest(Request request) {
            request.increment("page",1);
        }

        @Override
        public void getRefreshDataRequest(Request request) {
            request.put("page","0");
        }
    }

    class Bean{
        String text;
    }
}