package com.fastlib;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
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
    @Bind(R.id.memProgress)
    ProgressBar mProgress;

    @Override
    public void alreadyPrepared() {
        mThreadLayout.post(new Runnable() {
            @Override
            public void run() {
                int maxThreads = NetManager.sRequestPool.getMaximumPoolSize();
                int layoutWidth = mThreadLayout.getWidth();
                for (int i = 0; i < maxThreads; i++) {
                    View view = new View(MainActivity.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(layoutWidth / maxThreads-15,10);

                    lp.leftMargin = 15;
                    view.setLayoutParams(lp);
                    view.setBackgroundColor(getResources().getColor(R.color.grey_400));
                    mThreadLayout.addView(view);
                }
                ((MonitorThreadPool) NetManager.sRequestPool).setThreadStatusChangedListener(new MonitorThreadPool.OnThreadStatusChangedListener() {
                    @Override
                    public void onThreadStatusChanged(final int position, final int status) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (status == MonitorThreadPool.THREAD_STATUS_IDLE)
                                    mTaskCompleteCount.setText(String.format(Locale.getDefault(), "task complete:%d", NetManager.sRequestPool.getCompletedTaskCount()));
                                mThreadLayout.getChildAt(position - 1).setBackgroundColor(getResources().getColor(status == MonitorThreadPool.THREAD_STATUS_IDLE ? R.color.grey_400 : R.color.green_400));
                            }
                        });
                    }
                });
                NetManager.sRequestPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        while(!isFinishing()){
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Runtime runtime=Runtime.getRuntime();
                            long maxMem=runtime.maxMemory();
                            long totalMem=runtime.totalMemory();
                            long useMem=runtime.totalMemory()-runtime.freeMemory();
                            int applyPercent= (int) (totalMem*100/maxMem);
                            int usePercent= (int) (useMem*100/maxMem);
                            mProgress.setSecondaryProgress(applyPercent);
                            mProgress.setProgress(usePercent);
                        }
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

    List<byte[]> mData=new ArrayList<>();

    @Bind(R.id.bt)
    private void bt() {
        mData.add(new byte[1024*1024*5]);
        System.out.println(mData.size());
        net(new Request("get", "http://192.168.31.124:8080/Fastlib/upload/a.zip")
                .setDownloadable(new DefaultDownload(new File(getExternalCacheDir(), "a.zip")))
                .setListener(new SimpleListener<String>() {

                    @Override
                    public void onResponseListener(Request r, String result) {

                    }
                }));
    }

    private CpuTimer[] mLastCpuTimer;

    class CpuTimer{
        int idle; //空闲时长
        int count; //总时长
    }

    class SingleCpuInfo{
        String cpuName;
        float rate; //当前速率
    }

    @Bind(R.id.bt2)
    private void bt2() {
        try{
            BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream("/proc/stat")));
            for(int i = 0; i< Runtime.getRuntime().availableProcessors(); i++){
                String line=reader.readLine();
                String[] columns=line.replace("  "," ").split(" "); //分列
                if(columns.length<=1||TextUtils.isEmpty(columns[0])||!columns[0].startsWith("cpu")) break;
                int consumeCount=0;
                int idleTime=Integer.parseInt(columns[4]);
                for(int j=1;j<columns.length;j++){
                    consumeCount+=Float.parseFloat(columns[j]);
                }
                int rCount=consumeCount- mLastCpuTimer[i].count;
                int rIdle=idleTime- mLastCpuTimer[i].idle;
                int usageTime=rCount-rIdle;
                SingleCpuInfo info=new SingleCpuInfo();
                if(rCount==0)
                    rCount=1;
                info.cpuName=columns[0];
                info.rate=usageTime*100/rCount;
                mLastCpuTimer[i].idle=idleTime;
                mLastCpuTimer[i].count=consumeCount;
                if("intr".equals(info.cpuName)){
                    info.cpuName="休眠CPU";
                    info.rate=0;
                }
                System.out.println(info.rate);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        net(new Request("get", "http://www.baidu.com").setListener(new SimpleListener<String>() {
//            @Override
//            public void onResponseListener(Request r, String result) {
//
//            }
//        }));
    }

    @Event
    private void eDownloading(EventDownloading event) {
        mAdapter.getRequestByHash(mRequestHash.get(event.getRequest().hashCode())).downloading = event;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetManager.getInstance().setGlobalListener(null);
        ((MonitorThreadPool)NetManager.sRequestPool).setThreadStatusChangedListener(null);
        System.gc();
    }
}