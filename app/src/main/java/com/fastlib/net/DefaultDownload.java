package com.fastlib.net;

import java.io.File;

/**
 * 实例化一个简单的可下载类
 */
public class DefaultDownload implements Downloadable {
    private File mTargetFile;

    public DefaultDownload(File target){
        mTargetFile=target;
    }

    @Override
    public File getTargetFile() {
        return mTargetFile;
    }

    @Override
    public void setTargetFile(File f) {
        mTargetFile=f;
    }
}
