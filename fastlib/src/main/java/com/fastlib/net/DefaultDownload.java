package com.fastlib.net;

import java.io.File;
import java.io.IOException;

/**
 * 实例化一个简单的可下载类,默认不可中断,不修改文件名,不判断资源是否过期
 */
public class DefaultDownload implements Downloadable {
    private boolean mSupportBreak;
    private boolean mChangeIfHadName;
    private String mExpireTime;
    private File mTargetFile;
    private File mFinalFile; //如果文件夹名被修改，这个将是修改后的文件

    public DefaultDownload(String path){
        this(new File(path));
    }

    public DefaultDownload(File target){
        mSupportBreak=false;
        mChangeIfHadName=false;
        mTargetFile=target;
        ensureTargetFileExists();
    }

    /**
     * 确保目标文件生成
     */
    private void ensureTargetFileExists(){
        if(!mTargetFile.exists()){
            try {
                File parent=mTargetFile.getParentFile();
                parent.mkdirs();
                mTargetFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public DefaultDownload setTargetFile(File targetFile) {
        mTargetFile = targetFile;
        return this;
    }

    public DefaultDownload setSupportBreak(boolean supportBreak) {
        mSupportBreak = supportBreak;
        return this;
    }

    public DefaultDownload setChangeIfHadName(boolean changeIfHadName) {
        mChangeIfHadName = changeIfHadName;
        return this;
    }

    public DefaultDownload setExpireTime(String expireTime) {
        mExpireTime = expireTime;
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

    @Override
    public String expireTime() {
        return mExpireTime;
    }

    @Override
    public void setFinalFile(File finalFile){
        mFinalFile=finalFile;
    }

    public File getFinalFile(){
        return mFinalFile==null?mTargetFile:mFinalFile;
    }
}
