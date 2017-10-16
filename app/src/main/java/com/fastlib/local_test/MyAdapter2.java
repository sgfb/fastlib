package com.fastlib.local_test;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.fastlib.R;
import com.fastlib.adapter.FastAdapterForRecycler;
import com.fastlib.base.CommonViewHolder;

import java.util.List;

/**
 * Created by sgfb on 17/10/7.
 */

public class MyAdapter2 extends FastAdapterForRecycler<Bean2>{

    public MyAdapter2(Context context, List<Bean2> data) {
        super(context, R.layout.item3, data);
    }

    @Override
    public void binding(int position, Bean2 data, CommonViewHolder holder) {
        holder.setText(R.id.name,data.name);
        holder.setText(R.id.type, data.type);
        Glide.with(mContext).load(data.cover).into((ImageView)holder.getView(R.id.image));
    }
}
