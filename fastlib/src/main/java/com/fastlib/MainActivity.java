package com.fastlib;

<<<<<<< HEAD
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.annotation.Event;
import com.fastlib.app.module.FastActivity;
import com.fastlib.bean.event.EventDownloading;
import com.fastlib.net.DefaultDownload;
import com.fastlib.net.MonitorThreadPool;
import com.fastlib.net.NetManager;
import com.fastlib.net.Request;
import com.fastlib.net.listener.GlobalListener;
import com.fastlib.net.listener.SimpleListener;
import com.fastlib.utils.N;
import com.fastlib.utils.ScreenUtils;

import java.io.File;
import java.util.Locale;

/**
 * Create by sgfb on 2018/11/5.
 * E-mail:602687446@qq.com
 */
@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity {
    @Bind(R.id.list)
    RecyclerView mList;
    @Bind(R.id.threadLayout)
    LinearLayout mThreadLayout;
    @Bind(R.id.taskCompleteCount)
    TextView mTaskCompleteCount;
    RequestingAdapter mAdapter;
    SparseIntArray mRequestHash = new SparseIntArray();

    @Override
    public void alreadyPrepared() {
        int maxThreads = NetManager.sRequestPool.getMaximumPoolSize();
        int screenWidth = ScreenUtils.getScreenWidth()-15*(maxThreads-1);
        for (int i = 0; i < maxThreads; i++) {
            View view = new View(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(screenWidth / maxThreads, 12);

            if(i!=0)
                lp.leftMargin=15;
            view.setLayoutParams(lp);
            view.setBackgroundColor(getResources().getColor(R.color.grey_400));
            mThreadLayout.addView(view);
        }
        ((MonitorThreadPool)NetManager.sRequestPool).setThreadStatusChangedListener(new MonitorThreadPool.OnThreadStatusChangedListener() {
            @Override
            public void onThreadStatusChanged(final int position, final int status) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(status==MonitorThreadPool.THREAD_STATUS_IDLE)
                            mTaskCompleteCount.setText(String.format(Locale.getDefault(),"task complete:%d",NetManager.sRequestPool.getCompletedTaskCount()));
                        mThreadLayout.getChildAt(position-1).setBackgroundColor(getResources().getColor(status==MonitorThreadPool.THREAD_STATUS_IDLE?R.color.grey_400:R.color.green_400));
                    }
                });
            }
        });
        NetManager.getInstance().setGlobalListener(new GlobalListener() {
            @Override
            public void onLaunchRequestBefore(Request request) {
                int type = Requesting.TYPE_NORMAL;

                if (request.getDownloadable() != null)
                    type = Requesting.TYPE_DOWNLOADING;
                else if (request.getFiles() != null && !request.getFiles().isEmpty())
                    type = Requesting.TYPE_UPLOADING;
                Requesting requesting = new Requesting(Requesting.STATUS_WAITING, type, request.getUrl());
                mRequestHash.put(request.hashCode(), requesting.hashCode());
                mAdapter.addData(requesting);
            }

            @Override
            public void onRequestLaunched(final Request request) {
                Requesting requesting = mAdapter.getRequestByHash(mRequestHash.get(request.hashCode()));
                if (requesting != null)
                    requesting.status = Requesting.STATUS_REQUESTING;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public byte[] onRawData(Request r, byte[] data, long downloadSize, long uploadSize) {
                final Requesting requesting = mAdapter.getRequestByHash(mRequestHash.get(r.hashCode()));
                requesting.status = Requesting.STATUS_SUCCESS;
                requesting.contentLength = downloadSize;
                requesting.timeConsume = r.getResponseStatus().time;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });

                NetManager.sRequestPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.remove(requesting);
                            }
                        });
                    }
                });
                return super.onRawData(r, data, downloadSize, uploadSize);
            }

            @Override
            public String onErrorListener(Request r, String error) {
                final Requesting requesting = mAdapter.getRequestByHash(mRequestHash.get(r.hashCode()));
                requesting.status = Requesting.STATUS_ERROR;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });

                NetManager.sRequestPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.remove(requesting);
                            }
                        });
                    }
                });
                return super.onErrorListener(r, error);
            }
        });
        mList.setAdapter(mAdapter = new RequestingAdapter());
    }

    @Bind(R.id.bt)
    private void bt() {
        net(new Request("get", "http://www.baidu.com").setListener(new SimpleListener<String>() {
            @Override
            public void onResponseListener(Request r, String result) {

            }
        }));
    }

    @Bind(R.id.bt2)
    private void bt2() {
        net(new Request("get", "http://192.168.31.124:8080/Fastlib/upload/a.zip")
                .setDownloadable(new DefaultDownload(new File(getExternalCacheDir(), "a.zip")))
                .setListener(new SimpleListener<String>() {

                    @Override
                    public void onResponseListener(Request r, String result) {

                    }
                }));
    }

    @Event
    private void eDownloading(EventDownloading event) {
        mAdapter.getRequestByHash(mRequestHash.get(event.getRequest().hashCode())).downloading = event;
        mAdapter.notifyDataSetChanged();
    }
=======
import com.fastlib.R;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.base.AbsWebViewActivity;

import android.content.Intent;
import android.view.View;

import android.widget.ImageView;
import android.widget.Button;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity {

	@Bind(R.id.bt)
	public void onBt(View view){
		Intent intent=new Intent(this,WebViewActivity.class);
		intent.putExtra(WebViewActivity.ARG_STR_URL,"https://m.mafengwo.cn/movie/detail/442273.html");
		intent.putExtra(WebViewActivity.ARG_INT_WEBVIEW_ID,R.id.webView);
		startActivity(intent);
	}

	@Override
	public void alreadyPrepared() {

	}
>>>>>>> 0668ee551062ff822b88b19e387267a6b1b18971
}