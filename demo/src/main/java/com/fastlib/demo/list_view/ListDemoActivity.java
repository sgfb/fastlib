package com.fastlib.demo.list_view;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.aspect.base.AspectActivity;
import com.fastlib.aspect.ThreadOn;
import com.fastlib.aspect.exception.ExceptionHandler;
import com.fastlib.demo.R;
import com.fastlib.demo.app.CustomViewInject;

import java.util.List;

/**
 * Created by sgfb on 2020\03\01.
 */
@ContentView(R.layout.act_list)
public class ListDemoActivity extends AspectActivity<ListDemoView,ListDemoController> implements ExceptionHandler {
    @Bind(R.id.refresh)
    SwipeRefreshLayout mRefresh;

    @Override
    public void onException(Exception e) {
        e.printStackTrace();
    }

    @Override
    protected void onReady() {

    }

    @ThreadOn(ThreadOn.ThreadType.WORK)
    @Bind(value = R.id.list,type = CustomViewInject.TYPE_REMOTE_ADAPTER_ALL)
    private List<ItemBean> getItemBean(boolean isRefresh){
        return mController.getItemBean(isRefresh);
    }

    @Bind(value = R.id.refresh,type = CustomViewInject.TYPE_SWIPE_REFRESH)
    private void listRefresh(){
        mView.refreshList();
    }
}
