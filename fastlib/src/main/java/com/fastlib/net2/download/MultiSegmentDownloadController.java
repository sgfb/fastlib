package com.fastlib.net2.download;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastlib.app.EventObserver;
import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.db.And;
import com.fastlib.db.Condition;
import com.fastlib.db.FastDatabase;
import com.fastlib.db.FilterCommand;
import com.fastlib.net2.Request;
import com.fastlib.net2.core.HeaderDefinition;
import com.fastlib.net2.core.ResponseHeader;
import com.fastlib.net2.listener.SimpleListener;
import com.fastlib.utils.ContextHolder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

/**
 * Created by sgfb on 2020\02\20.
 * 多线程分块下载
 * 提示：一般不限制下载速度的资源使用单线程和多线程是差不多的,所以应优先使用单线程下载,遇到限速的资源再考虑使用多线程下载
 * 应当注意有些下载资源是没有总长度的,目前不支持这种资源多线程下载
 * TODO 请求参数复制、续传、速度监控
 */
public class MultiSegmentDownloadController extends SimpleDownloadController {
    private final static int BLOCK_SIZE = 64 * 1024; //块大小,KB单位,用来做续传和速度监控
    private final static int DEFAULT_THREAD_COUNT = Math.max(2, ThreadPoolManager.sSlowPool.getMaximumPoolSize() / 2);
    private final static int BUFFER_SIZE = 4096;
    private final static int RECORD_APPEND_INTERVAL = 1000;
    public final static Map<String, BitSet> sDownloadStatus = new HashMap<>();
    private int mStartPoint;

    public MultiSegmentDownloadController(@NonNull File targetFile, int startPoint) {
        this(targetFile, false, false, startPoint);
    }

    public MultiSegmentDownloadController(@NonNull File targetFile, boolean useServerFilename, boolean append, int startPoint) {
        super(targetFile, useServerFilename, append);
        mStartPoint = startPoint;
    }

    @Override
    protected void onDownloadReady(final File toFile, InputStream inputStream, @Nullable String filename, final long length) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(toFile.getAbsolutePath(), "rw");
        randomAccessFile.skipBytes(mStartPoint);

        BitSet bitSet = sDownloadStatus.get(toFile.getAbsolutePath());
        if (bitSet == null)
            sDownloadStatus.put(toFile.getAbsolutePath(), bitSet = new BitSet());

        final int startBlockIndex = mStartPoint / BLOCK_SIZE;
        int unrecordedBlockSize = 0;
        int recordedBlockSize = 0;

        byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            randomAccessFile.write(buffer, 0, len);
            unrecordedBlockSize += len;
            while (unrecordedBlockSize > BLOCK_SIZE) {
                unrecordedBlockSize -= BLOCK_SIZE;
                bitSet.set(startBlockIndex + recordedBlockSize);
                recordedBlockSize++;
            }
        }
        if(unrecordedBlockSize>0)
            bitSet.set(startBlockIndex+recordedBlockSize);
        randomAccessFile.close();
    }

    private void recordToDb(File toFile, long timer, long downloadedCount) {
        if (supportAppend && ContextHolder.getContext() != null && System.currentTimeMillis() > (timer + RECORD_APPEND_INTERVAL)) {
            timer = System.currentTimeMillis();
            MultiDownloadProgress multiDownloadProgress = FastDatabase.getDefaultInstance(ContextHolder.getContext())
                    .addFilter(And.condition(Condition.equal(toFile.getAbsolutePath())))
                    .getFirst(MultiDownloadProgress.class);
            if (multiDownloadProgress == null)
                multiDownloadProgress = new MultiDownloadProgress(toFile.getAbsolutePath());
            MultiDownloadPoint currPoint = null;
            for (MultiDownloadPoint point : multiDownloadProgress.downloadedSegment) {
                if (point.start <= mStartPoint && point.end >= mStartPoint) {
                    currPoint = point;
                    multiDownloadProgress.downloadedSegment.remove(point);
                    break;
                }
            }
            if (currPoint == null)
                currPoint = new MultiDownloadPoint(mStartPoint);
            currPoint.end = mStartPoint + downloadedCount;
            multiDownloadProgress.downloadedSegment.add(currPoint);
            Collections.sort(multiDownloadProgress.downloadedSegment);
            FastDatabase.getDefaultInstance(ContextHolder.getContext()).saveOrUpdate(multiDownloadProgress);
        }
    }

    /**
     * 本方法只能在工作线程调起
     */
    public static void startMultiDownload(Request request, final File file) throws Exception {
        if (Thread.currentThread() == Looper.getMainLooper().getThread())
            throw new IllegalStateException("多线程下载只能在工作线程中启动");

        Request requestHead = request;
        requestHead.setMethod("head");
        requestHead.startSyc(String.class);
        ResponseHeader header = requestHead.getResponseHeader();
        long length = -1;
        if (header != null) {
            try {
                length = Long.parseLong(header.getHeaderFirst(HeaderDefinition.KEY_CONTENT_LENGTH));
            } catch (NumberFormatException e) {
                //不处理
            }
        }

        if (length != -1) {
            BitSet bitSet=new BitSet();
            bitSet.set((int) (length/BLOCK_SIZE),true);
            sDownloadStatus.put(file.getAbsolutePath(),bitSet);

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.setLength(length);
            randomAccessFile.close();

            DownloadMonitor monitor = new DownloadMonitor() {
                @Override
                protected void onDownloading(long downloadedOneInterval) {
                    System.out.println(downloadedOneInterval / 1024 + "KB/s");
                    EventObserver.getInstance().sendEvent(ContextHolder.getContext(),mFile.getAbsolutePath());
                }

                @Override
                protected long downloadedSize() {
                    BitSet bitSet = sDownloadStatus.get(mFile.getAbsolutePath());
                    if (bitSet != null) {
                        return bitSet.cardinality() * BLOCK_SIZE;
                    }
                    else return 0;
                }
            };
            monitor.setFile(file);
            monitor.start();

            final CountDownLatch latch = new CountDownLatch(DEFAULT_THREAD_COUNT);
            for (int i = 0, startPoint, endPoint = 0; i < DEFAULT_THREAD_COUNT; i++) {
                startPoint = endPoint;
                if (startPoint > 0)
                    startPoint += 1;
                endPoint += length / DEFAULT_THREAD_COUNT;

                Request segmentRequest = new Request(request.getUrl());
                System.out.println("下载起止 " + startPoint + " " + endPoint);
                segmentRequest.addHeader("Range", "bytes=" + startPoint + "-" + endPoint);
                segmentRequest.setDownloadable(new MultiSegmentDownloadController(file, startPoint));
                segmentRequest.setReadTimeout(30000);
                segmentRequest.setListener(new SimpleListener<Void>() {

                    @Override
                    public void onResponseSuccess(Request request, Void result) {
                        latch.countDown();
                    }

                    @Override
                    public void onError(Request request, Exception error) {
                        super.onError(request, error);
                        latch.countDown();
                    }
                });
                segmentRequest.start();

            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            monitor.stop();
        }
    }
}
