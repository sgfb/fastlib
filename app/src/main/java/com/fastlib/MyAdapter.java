package com.fastlib;

import android.content.Context;

import com.fastlib.adapter.FastAdapter;
import com.fastlib.base.OldViewHolder;
import com.fastlib.ehome.Contact;

import java.util.List;

/**
 * Created by sgfb on 17/7/3.
 */

public class MyAdapter extends FastAdapter<Contact>{

    public MyAdapter(Context context, List<Contact> data) {
        super(context,R.layout.item_2, data);
    }

    @Override
    public void binding(int position, Contact data, OldViewHolder holder) {
        holder.setText(R.id.name,data.name);
        holder.setText(R.id.phone,data.phone);
        holder.setText(R.id.address,data.address);
        holder.setText(R.id.description,data.description);
        holder.setText(R.id.email,data.email);
    }
}
