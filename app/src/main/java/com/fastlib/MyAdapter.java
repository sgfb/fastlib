package com.fastlib;

import android.content.Context;
import android.support.v4.util.Pair;
import android.widget.ImageView;

import com.fastlib.adapter.FastAdapter;
import com.fastlib.base.OldViewHolder;

import java.util.List;

/**
 * Created by sgfb on 2017/11/5.
 */
public class MyAdapter extends FastAdapter<Pair<Boolean,String>>{

    public MyAdapter(Context context) {
        super(context,R.layout.item);
    }

    public MyAdapter(Context context, List<Pair<Boolean, String>> data) {
        super(context, R.layout.item, data);
    }

    @Override
    public void binding(int position, Pair<Boolean, String> data, OldViewHolder holder){
        ImageView imageView=holder.getView(R.id.icon);
        holder.setText(R.id.title,data.second);
        imageView.setImageResource(data.first?R.drawable.file:R.drawable.folder);
    }
}
