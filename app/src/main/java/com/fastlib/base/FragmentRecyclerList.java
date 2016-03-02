package com.fastlib.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fastlib.R;
import com.fastlib.widget.RecycleListView;

/**
 * Created by sgfb on 16/2/26.
 *
 * 带Toolbar的RecyclerList简单封装
 */
public abstract class FragmentRecyclerList extends Fragment{
    protected Toolbar mToolbar;
    protected RecycleListView mList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v=inflater.inflate(R.layout.fragment_recycler_list,null);
        mToolbar=(Toolbar)v.findViewById(R.id.toolbar);
        mList=(RecycleListView)v.findViewById(R.id.recyclerList);
        mList.setAdapter(getRecyclerAdapter());
        return v;
    }

    public abstract RecyclerView.Adapter<? extends RecyclerView.ViewHolder> getRecyclerAdapter();
}
