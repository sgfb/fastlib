package com.fastlib.net;

import java.io.File;
import java.io.IOException;

/**
 * 实例化一个简单的可下载类,默认不可中断
 */
public class DefaultDownload implements Downloadable {
    private File mTargetFile;
    private boolean mSupportBreak;
    private boolean mChangeIfHadName;

    public DefaultDownload(String path){
        this(new File(path));
    }

    public DefaultDownload(File target){
        this(target,false);
    }

    public DefaultDownload(File target,boolean supportBreak){
        this(target,false,true);
    }

    public DefaultDownload(File target,boolean supportBreak,boolean changeIfHadName){
        mTargetFile=target;
        mSupportBreak=supportBreak;
        if(!mTargetFile.exists())
            try {
                mTargetFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    @Override
    public boolean changeIfHadName() {
        return mChangeIfHadName;
    }
}
