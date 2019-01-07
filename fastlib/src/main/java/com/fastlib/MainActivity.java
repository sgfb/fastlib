package com.fastlib;

import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.annotation.Event;
import com.fastlib.app.module.FastActivity;
import com.fastlib.bean.event.EventDownloading;
import com.fastlib.net.DefaultDownload;
import com.fastlib.net.NetManager;
import com.fastlib.net.Request;
import com.fastlib.net.listener.GlobalListener;
import com.fastlib.net.listener.SimpleListener;

import java.io.File;

/**
 * Create by sgfb on 2018/11/5.
 * E-mail:602687446@qq.com
 */
@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity {
    @Bind(R.id.list)
    RecyclerView mList;
    RequestingAdapter mAdapter;
    SparseIntArray mRequestHash = new SparseIntArray();

    @Override
    public void alreadyPrepared() {
        NetManager.getInstance().setGlobalListener(new GlobalListener() {
            @Override
            public void onLauncherRequestBefore(Request request) {
                int type = Requesting.TYPE_NORMAL;

                if (request.getDownloadable() != null)
                    type = Requesting.TYPE_DOWNLOADING;
                else if (request.getFiles() != null && !request.getFiles().isEmpty())
                    type = Requesting.TYPE_UPLOADING;
                Requesting requesting = new Requesting(Requesting.STATUS_REQUESTING, type, request.getUrl());
                mRequestHash.put(request.hashCode(), requesting.hashCode());
                mAdapter.addData(requesting);
            }

            @Override
            public byte[] onRawData(Request r, byte[] data,long downloadSize,long uploadSize){
                final Requesting requesting = mAdapter.getRequestByHash(mRequestHash.get(r.hashCode()));
                requesting.status = Requesting.STATUS_SUCCESS;
                requesting.contentLength=downloadSize;
                requesting.timeConsume=r.getResponseStatus().time;
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
                return super.onRawData(r, data,downloadSize,uploadSize);
            }

            @Override
            public String onErrorListener(Request r, String error){
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
    private void eDownloading(EventDownloading event){
        mAdapter.getRequestByHash(mRequestHash.get(event.getRequest().hashCode())).downloading=event;
        mAdapter.notifyDataSetChanged();
    }
}