package com.fastlib.demo;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastFragment;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by sgfb on 2020\03\08.
 */
@ContentView(R.layout.frag_list)
public class ListFragment extends FastFragment{
    @Bind(R.id.list)
    RecyclerView mList;
    MyAdapter mAdapter;

    @Override
    public void alreadyPrepared(){
        mList.setAdapter(mAdapter=new MyAdapter());
    }
}