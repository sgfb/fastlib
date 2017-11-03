package com.fastlib;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.fastlib.adapter.FastAdapterForRecycler;
import com.fastlib.base.CommonViewHolder;

import java.util.List;

/**
 * Created by sgfb on 2017/11/2.
 */
public class MyAdapter extends FastAdapterForRecycler<Bean>{

    public MyAdapter(Context context) {
        super(context,R.layout.item_course);
    }

    public MyAdapter(Context context, List<Bean> data) {
        super(context,R.layout.item_course, data);
    }

    @Override
    public void binding(final int position, final Bean data, CommonViewHolder holder) {
        holder.setVisibility(R.id.text,data.isTitle? View.VISIBLE:View.INVISIBLE);
        holder.setVisibility(R.id.courseTag,data.isTitle?View.INVISIBLE: View.VISIBLE);
        holder.setText(R.id.text,data.title);
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(data.title)){
                    data.isTitle=!data.isTitle;
                    notifyDataSetChanged();
                }
            }
        });
    }
}
