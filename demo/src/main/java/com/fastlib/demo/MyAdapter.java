package com.fastlib.demo;

import com.fastlib.adapter.BaseRecyAdapter;
import com.fastlib.annotation.ContentView;
import com.fastlib.base.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 2020\03\08.
 */
@ContentView(R.layout.item_test)
public class MyAdapter extends BaseRecyAdapter<String>{

    public MyAdapter(){
        super();
        List<String> list=new ArrayList<>();
        for(int i=0;i<100;i++)
            list.add(Integer.toString(i));
        setData(list);
    }

    @Override
    public void binding(int position, String data, RecyclerViewHolder holder) {

    }
}
