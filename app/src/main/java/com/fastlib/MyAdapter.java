package com.fastlib;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fastlib.adapter.FastAdapterForRecycler;
import com.fastlib.base.CommonViewHolder;

import java.util.List;

/**
 * Created by sgfb on 18/1/5.
 */
public class MyAdapter extends FastAdapterForRecycler<String>{

    public MyAdapter(Context context, List<String> data) {
        super(context,R.layout.item, data);
    }

    @Override
    public void binding(int position, String data, CommonViewHolder holder) {
        holder.setText(R.id.text,"position:"+position);
        Glide.with(mContext).load("https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=2338511376,2933014988&fm=173&s=4CC2EA1A5743754B04C41DD8020010B2&w=550&h=992&img.JPEG")
                .into((ImageView)holder.getView(R.id.image));
    }
}