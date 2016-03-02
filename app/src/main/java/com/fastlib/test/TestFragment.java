package com.fastlib.test;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fastlib.R;
import com.fastlib.base.FragmentRecyclerList;

/**
 * Created by sgfb on 16/2/26.
 */
public class TestFragment extends FragmentRecyclerList{

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mToolbar.setTitle("title");
        mToolbar.setBackgroundColor(getResources().getColor(R.color.Blue_800));
    }

    @Override
    public RecyclerView.Adapter<? extends RecyclerView.ViewHolder> getRecyclerAdapter() {
        return new MyAdapter();
    }

    class MyAdapter extends RecyclerView.Adapter<Holder>{

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(new TextView(getContext()));
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.tv.setText(Integer.toString(position*1111));
        }

        @Override
        public int getItemCount() {
            return 10;
        }
    }

    class Holder extends RecyclerView.ViewHolder{
        TextView tv;

        public Holder(View itemView){
            super(itemView);
            tv=(TextView)itemView;
        }
    }
}
