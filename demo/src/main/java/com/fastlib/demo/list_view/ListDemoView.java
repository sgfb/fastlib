package com.fastlib.demo.list_view;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.fastlib.adapter.RemoteBindAdapter;
import com.fastlib.annotation.Bind;
import com.fastlib.aspect.base.OptionalInit;
import com.fastlib.demo.R;
import com.fastlib.utils.fitout.AutoFit;

/**
 * Created by sgfb on 2020\03\02.
 */
public class ListDemoView {
    @Bind(R.id.refresh)
    SwipeRefreshLayout mRefresh;
    @AutoFit(attachment = RemoteBindAdapterDemo.class)
    @Bind(R.id.list)
    RecyclerView mList;

    @OptionalInit
    private void init(){
        mRefresh.setRefreshing(true);
        ((RemoteBindAdapter)mList.getAdapter()).setOnRemoteAnswerListener(new RemoteBindAdapter.OnRemoteAnswerListener() {
            @Override
            public void onAnswered() {
                mRefresh.setRefreshing(false);
            }
        });
    }

    public void refreshList(){
        ((RemoteBindAdapter)mList.getAdapter()).refresh();
    }
}
