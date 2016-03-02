package com.fastlib.net;

import java.io.File;

/**
 * 实例化一个简单的可下载类
 */
public class DefaultDownload implements Downloadable {
    private File mTargetFile;
    private DownloadCompleteListener mListener;

    public DefaultDownload(File target){
        this(target,null);
    }

    public DefaultDownload(File target,DownloadCompleteListener listener){
        mTargetFile=target;
        mListener=listener;
    }

    @Override
    public File getTargetFile() {
        return mTargetFile;
    }

    @Override
    public void setTargetFile(File f) {
        mTargetFile=f;
    }

    @Override
    public DownloadCompleteListener getCompleteListener() {
        return mListener;
    }

    @Override
    public void setDownloadCompleteListener(DownloadCompleteListener l) {
        mListener=l;
    }
}
