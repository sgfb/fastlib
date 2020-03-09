package com.fastlib.demo.net;

import androidx.recyclerview.widget.RecyclerView;

import com.fastlib.adapter.BaseRecyAdapter;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.base.RecyclerViewHolder;
import com.fastlib.demo.R;
import com.fastlib.net2.utils.RequestAgentFactory;
import com.fastlib.utils.N;

/**
 * Created by sgfb on 2020\03\06.
 */
@ContentView(R.layout.act_cloud)
public class CloudActivity extends FastActivity {
    @Bind(R.id.list)
    RecyclerView mList;
    CloudAdapter mAdapter;
    CloudInterface mCloudModel = RequestAgentFactory.genAgent(CloudInterface.class);
    String mDownloadUrl;

    @Override
    public void alreadyPrepared() {
        mList.setAdapter(mAdapter = new CloudAdapter());
        mAdapter.setOnItemClickListener(new BaseRecyAdapter.OnItemClickListener<CloudFile>() {
            @Override
            public void onItemClick(int position, RecyclerViewHolder holder, CloudFile data) {
                mDownloadUrl = data.downloadUrl;
                N.showShort(CloudActivity.this, "已选中:" + data.fileName);
            }
        });
    }

    @Bind(R.id.bt)
    private void download() {

    }

    @Bind(R.id.bt2)
    private void upload() {

    }
}
