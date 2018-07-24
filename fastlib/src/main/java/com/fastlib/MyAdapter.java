package com.fastlib;

import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;

import com.fastlib.adapter.BaseRecyAdapter;
import com.fastlib.annotation.ContentView;
import com.fastlib.base.CommonViewHolder;

/**
 * Created by sgfb on 2018/7/15.
 */
@ContentView(R.layout.item)
public class MyAdapter extends BaseRecyAdapter<Integer>{

    @Override
    public void binding(int position, Integer data, CommonViewHolder holder) {
        ImageView iv=holder.getView(R.id.image);
        iv.setImageDrawable(new ColorDrawable(holder.getConvertView().getResources().getColor(position%2==0?R.color.green_500:R.color.yellow_500)));
    }
}
