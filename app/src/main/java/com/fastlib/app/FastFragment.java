package com.fastlib.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.fastlib.net.Request;
import com.fastlib.utils.ViewInject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by sgfb on 17/1/31.
 */
public class FastFragment extends Fragment{
    private List<Request> mRequests=new ArrayList<>();
    protected ThreadPoolExecutor mThreadPool= (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThreadPool.execute(new Runnable(){
            @Override
            public void run(){
                EventObserver.getInstance().subscribe(this);
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewInject.inject(this,mThreadPool);
    }

    /**
     * 启动网络请求
     * @param request
     */
    protected void net(Request request){
        if(!mRequests.contains(request))
            mRequests.add(request);
        request.setHost(this).setExecutor(mThreadPool).start(false);
    }

    public void addRequest(Request request){
        if(!mRequests.contains(request))
            mRequests.add(request);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        EventObserver.getInstance().unsubscribe(this);
        mThreadPool.shutdownNow();
        mThreadPool.purge();
        for(Request request:mRequests)
            request.clear();
    }
}