package com.fastlib;

import android.view.View;
import android.view.ViewGroup;

import com.fastlib.adapter.CommonBaseAdapter;
import com.fastlib.adapter.FastAdapter;
import com.fastlib.annotation.ContentView;
import com.fastlib.base.OldViewHolder;

/**
 * Created by sgfb on 18/7/30.
 */
@ContentView(android.R.layout.simple_list_item_1)
public class MyAdapter extends FastAdapter<String> {

    @Override
    public void binding(int position, String data, OldViewHolder holder) {
        holder.setText(android.R.id.text1,data);
    }
}
