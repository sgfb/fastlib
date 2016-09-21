package com.fastlib.net;

import java.io.File;

/**
 * 实例化一个简单的可下载类,默认不可中断
 */
public class DefaultDownload implements Downloadable {
    private File mTargetFile;
    private boolean mSupportBreak;

    public DefaultDownload(File target){
        mTargetFile=target;
    }

    public DefaultDownload(File target,boolean supportBreak){
        mTargetFile=target;
        mSupportBreak=supportBreak;
    }

    public DefaultDownload setSupport(boolean support){
        mSupportBreak=support;
        return this;
    }

    @Override
    public File getTargetFile() {
        return mTargetFile;
    }

    @Override
    public boolean supportBreak() {
        return mSupportBreak;
    }
}
