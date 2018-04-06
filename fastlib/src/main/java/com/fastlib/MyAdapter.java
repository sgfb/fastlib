package com.fastlib;

import com.fastlib.adapter.BaseRecyAdapter;
import com.fastlib.annotation.ContentView;
import com.fastlib.base.CommonViewHolder;

/**
 * Created by sgfb on 18/4/6.
 */
@ContentView(android.R.layout.simple_list_item_1)
public class MyAdapter extends BaseRecyAdapter<String>{

    @Override
    public void binding(int position, String data, CommonViewHolder holder) {
        holder.setText(android.R.id.text1,data);
    }
}
