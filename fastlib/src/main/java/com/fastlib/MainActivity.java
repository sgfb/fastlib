package com.fastlib;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.ListView;

import com.fastlib.adapter.BaseRecyAdapter;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.base.CommonViewHolder;
import com.fastlib.net.Request;
import com.fastlib.net.SimpleListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 18/4/4.
 */
@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    @Bind(R.id.list)
    RecyclerView mList;
    MyAdapter mAdapter;

    @Override
    protected void alreadyPrepared() {
        List<String> list=new ArrayList<>();
        for(int i=0;i<40;i++)
            list.add(Integer.toString(i));

        mList.setAdapter(mAdapter=new MyAdapter());
        mAdapter.setData(list);
        mAdapter.setOnItemClickListener(new BaseRecyAdapter.OnItemClickListener<String>() {
            @Override
            public void onItemClick(int position, CommonViewHolder holder, String data) {
                System.out.println("data:"+data);
            }
        });
    }

    @Bind(R.id.bt)
    private void commit(){

    }
}