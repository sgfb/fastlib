package com.fastlib.demo.list_view;

import com.fastlib.adapter.RemoteBindAdapter;
import com.fastlib.annotation.ContentView;
import com.fastlib.base.RecyclerViewHolder;
import com.fastlib.demo.R;

import java.util.List;

/**
 * Created by sgfb on 2020\03\02.
 */
@ContentView(R.layout.item_bean)
public class RemoteBindAdapterDemo extends RemoteBindAdapter<ItemBean,List<ItemBean>> {

    @Override
    protected List<ItemBean> translate(List<ItemBean> resultData) {
        return resultData;
    }

    @Override
    public void binding(int position, ItemBean data, RecyclerViewHolder holder) {
        holder.setText(R.id.age,Integer.toString(data.age));
        holder.setText(R.id.name,data.name);
    }
}
