package com.fastlib.demo.net;

import com.fastlib.adapter.BaseRecyAdapter;
import com.fastlib.annotation.ContentView;
import com.fastlib.base.RecyclerViewHolder;
import com.fastlib.demo.R;

/**
 * Created by sgfb on 2020\03\06.
 */
@ContentView(R.layout.item_cloud)
public class CloudAdapter extends BaseRecyAdapter<CloudFile> {

    @Override
    public void binding(int position, CloudFile data, RecyclerViewHolder holder) {
        holder.setText(R.id.name,data.fileName);
        holder.setText(R.id.size,data.fileSize+"B");
    }
}
