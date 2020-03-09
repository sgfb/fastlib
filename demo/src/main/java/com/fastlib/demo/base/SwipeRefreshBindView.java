package com.fastlib.demo.base;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.View;

import com.fastlib.utils.bind_view.SimpleBindViewReceiver;

/**
 * Created by sgfb on 2020\03\03.
 */
public class SwipeRefreshBindView extends SimpleBindViewReceiver implements SwipeRefreshLayout.OnRefreshListener{

    @Override
    public void bindView(View view) {
        ((SwipeRefreshLayout)view).setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        if(mCallback!=null) mCallback.invokeSync();
    }
}
