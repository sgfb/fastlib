package com.fastlib.net2.download;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fastlib.db.SaveUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sgfb on 2020\02\20.
 * 单线程,可选覆盖原文件
 */
public class SingleDownloadController extends SimpleDownloadController{
    private DownloadMonitor mMonitor;
    private boolean isDownloadEnd;

    public SingleDownloadController(@NonNull File targetFile) {
        super(targetFile);
    }

    public SingleDownloadController(@NonNull File targetFile, boolean useServerFilename, boolean append) {
        super(targetFile, useServerFilename, append);
    }

    @Override
    protected void onDownloadReady(final File toFile, InputStream inputStream, @Nullable String filename, final long length) throws IOException{
        try{
            if(mMonitor!=null){
                mMonitor.setFile(toFile);
                mMonitor.setExpectDownloadSize(length);
                mMonitor.start();
            }
            SaveUtil.saveToFile(toFile,inputStream,supportAppend);
        } finally {
            if(mMonitor!=null) mMonitor.stop();
        }
    }

    public void setDownloadMonitor(DownloadMonitor monitor){
        mMonitor=monitor;
    }
}
