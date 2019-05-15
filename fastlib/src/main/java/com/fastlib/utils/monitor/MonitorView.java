package com.fastlib.utils.monitor;

import android.content.Context;
import android.os.Process;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fastlib.R;
import com.fastlib.annotation.Event;
import com.fastlib.app.EventObserver;
import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.bean.event.EventDownloading;
import com.fastlib.app.task.MonitorThreadPool;
import com.fastlib.net.NetManager;
import com.fastlib.net.Request;
import com.fastlib.net.listener.GlobalListener;
import com.fastlib.utils.ContextHolder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MonitorView{
    private ThreadPoolExecutor mThreadPool= (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private TextView mTaskCompleteCount;
    private ProgressBar mCpuProgress;
    private ProgressBar mMemProgress;
    private RecyclerView mRequestingList;
    private ViewGroup mThreadLayout;
    private View mView;

    private boolean isRunning=true;
    private long mLastAllCpuConsume;
    private long mMyLastCpuConsume;
    private RequestingAdapter mAdapter;
    private SparseIntArray mRequestHash = new SparseIntArray();

    public MonitorView(Context context){
        mView=LayoutInflater.from(context).inflate(R.layout.dialog_monitor,null);
        mThreadLayout=mView.findViewById(R.id.threadLayout);
        mTaskCompleteCount=mView.findViewById(R.id.taskCompleteCount);
        mCpuProgress=mView.findViewById(R.id.cpuProgress);
        mMemProgress=mView.findViewById(R.id.memProgress);
        mRequestingList=mView.findViewById(R.id.list);
        mRequestingList.setAdapter(mAdapter=new RequestingAdapter());
        EventObserver.getInstance().subscribe(context,this);
        start(context);
    }

    public View getView(){
        return mView;
    }

    private void start(final Context context){
        mThreadLayout.post(new Runnable() {
            @Override
            public void run() {
                //线程池监控
                int maxThreads = ThreadPoolManager.getThreadCount();
                int layoutWidth = mThreadLayout.getWidth();
                for (int i = 0; i < maxThreads; i++) {
                    View view = new View(context);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(layoutWidth / maxThreads - 15, 5);

                    lp.rightMargin = 15;
                    view.setLayoutParams(lp);
                    view.setBackgroundColor(context.getResources().getColor(R.color.grey_400));
                    mThreadLayout.addView(view);
                }
                ThreadPoolManager.setOnThreadChanageListener(new MonitorThreadPool.OnThreadStatusChangedListener() {
                    @Override
                    public void onThreadStatusChanged(final int position, final int status) {
                        mThreadLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                if (status == MonitorThreadPool.THREAD_STATUS_IDLE)
                                    mTaskCompleteCount.setText(String.format(Locale.getDefault(), "tc:%d", ThreadPoolManager.getCompleteTaskCount()));
                                mThreadLayout.getChildAt(position - 1).setBackgroundColor(context.getResources().getColor(status == MonitorThreadPool.THREAD_STATUS_IDLE ? R.color.grey_400 : R.color.green_400));
                            }
                        });
                    }
                });
                //内存监控
                new Thread(){
                    @Override
                    public void run() {
                        while (isRunning) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Runtime runtime = Runtime.getRuntime();
                            long maxMem = runtime.maxMemory();
                            long totalMem = runtime.totalMemory();
                            long useMem = runtime.totalMemory() - runtime.freeMemory();
                            int applyPercent = (int) (totalMem * 100 / maxMem);
                            int usePercent = (int) (useMem * 100 / maxMem);
                            mMemProgress.setSecondaryProgress(applyPercent);
                            mMemProgress.setProgress(usePercent);
                        }
                    }
                }.start();
                //cpu监控
                new Thread(){

                    @Override
                    public void run() {
                        try {
                            while (isRunning) {
                                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/stat")));
                                String line = in.readLine();
                                String[] cpuInfos = line.split(" ");
                                long allCpuCount = 0;
                                for (int i = 2; i <= 8; i++)
                                    allCpuCount += Long.parseLong(cpuInfos[i]);

                                BufferedReader myAppStatIn = new BufferedReader(new InputStreamReader(new FileInputStream(String.format(Locale.getDefault(), "/proc/%d/stat", Process.myPid()))));
                                String[] mycpuInfos = myAppStatIn.readLine().split(" ");
                                long myCpuSum = 0;
                                for (int i = 13; i <= 16; i++)
                                    myCpuSum += Long.parseLong(mycpuInfos[i]);
                                in.close();
                                myAppStatIn.close();

                                final int myAppCpuConsumePercent = (int) ((myCpuSum - mMyLastCpuConsume) * 100 / (allCpuCount - mLastAllCpuConsume));
                                mLastAllCpuConsume = allCpuCount;
                                mMyLastCpuConsume = myCpuSum;
                                mThreadLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mCpuProgress.setProgress(myAppCpuConsumePercent);
                                    }
                                });
                                Thread.sleep(1000);
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        //网络请求列表监控
        NetManager.getInstance().setGlobalListener(new GlobalListener() {
            @Override
            public void onLaunchRequestBefore(Request request) {
                int type = Requesting.TYPE_NORMAL;

                if (request.getDownloadable() != null)
                    type = Requesting.TYPE_DOWNLOADING;
                else if (request.getFiles() != null && !request.getFiles().isEmpty())
                    type = Requesting.TYPE_UPLOADING;
                final Requesting requesting = new Requesting(Requesting.STATUS_WAITING, type, request.getUrl());
                mRequestHash.put(request.hashCode(), requesting.hashCode());
                mThreadLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addData(requesting);
                    }
                });
            }

            @Override
            public void onRequestLaunched(final Request request) {
                Requesting requesting = mAdapter.getRequestByHash(mRequestHash.get(request.hashCode()));
                if (requesting != null)
                    requesting.status = Requesting.STATUS_REQUESTING;
                mThreadLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public byte[] onRawData(Request r, byte[] data, long downloadSize, long uploadSize) {
                final Requesting requesting = mAdapter.getRequestByHash(mRequestHash.get(r.hashCode()));

                if(requesting!=null){
                    requesting.status = Requesting.STATUS_SUCCESS;
                    requesting.contentLength = downloadSize;
                    requesting.timeConsume = r.getResponseStatus().time;
                    mThreadLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });

                    mThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mThreadLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.remove(requesting);
                                }
                            });
                        }
                    });
                }
                return super.onRawData(r, data, downloadSize, uploadSize);
            }

            @Override
            public Exception onErrorListener(Request r, Exception error) {
                final Requesting requesting = mAdapter.getRequestByHash(mRequestHash.get(r.hashCode()));
                requesting.status = Requesting.STATUS_ERROR;
                mThreadLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });

                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mThreadLayout.post(new Runnable() {
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
    }

    public void stop(){
        isRunning=false;
        mView=null;
        NetManager.getInstance().setGlobalListener(null);
        EventObserver.getInstance().unsubscribe(ContextHolder.getContext(),this);
        ThreadPoolManager.setOnThreadChanageListener(null);
    }

    @Event
    private void eDownloading(EventDownloading event) {
        mAdapter.getRequestByHash(mRequestHash.get(event.getRequest().hashCode())).downloading = event;
        mAdapter.notifyDataSetChanged();
    }
}
