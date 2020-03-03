package com.fastlib.demo.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fastlib.adapter.OnRemoteRequestResultListener;
import com.fastlib.adapter.RemoteBindAdapter;
import com.fastlib.app.AsyncCallback;
import com.fastlib.utils.bind_view.SimpleBindViewReceiver;

/**
 * Created by sgfb on 2020\03\03.
 */
public class RemoteAdapterBindView extends SimpleBindViewReceiver implements RemoteBindAdapter.OnLoadPageListener{

    @Override
    public void bindView(View view){
        RecyclerView recyclerView= (RecyclerView) view;
        RemoteBindAdapter adapter= (RemoteBindAdapter) recyclerView.getAdapter();
        adapter.setOnLoadPageListener(this,true);
    }

    @Override
    public void onRefreshPageListener(final OnRemoteRequestResultListener listener) {
        if(mCallback!=null){
            mCallback.invokeAsync(new AsyncCallback() {
                @Override
                public void callback(Object result) {
                    listener.onRemoteDataResult(result);
                }
            },true);
        }
    }

    @Override
    public void onLoadNextPageListener(final OnRemoteRequestResultListener listener) {
        if(mCallback!=null){
            mCallback.invokeAsync(new AsyncCallback() {
                @Override
                public void callback(Object result) {
                    listener.onRemoteDataResult(result);
                }
            },false);
        }
    }
}
