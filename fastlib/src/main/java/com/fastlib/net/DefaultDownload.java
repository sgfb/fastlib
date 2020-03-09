package com.fastlib.net;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * 实例化一个简单的可下载类,默认不可中断,不修改文件名,不判断资源是否过期
 */
public class DefaultDownload implements Downloadable {
    private boolean mChangeIfHadName;
    private String mExpireTime;
    private File mTargetFile;
    private DownloadSegment mDownloadSegment;

    public DefaultDownload(String path){
        this(new File(path));
    }

    public DefaultDownload(File target){
        mChangeIfHadName=false;
        mTargetFile=target;
        ensureTargetFileExists();
    }

    /**
     * 确保目标文件生成
     */
    private void ensureTargetFileExists() {
        if (!mTargetFile.exists()) {
            try {
                File parent = mTargetFile.getParentFile();
                parent.mkdirs();
                mTargetFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public DefaultDownload setDownloadSegment(boolean breakMode){
        mDownloadSegment=breakMode?new DownloadSegment():null;
        return this;
    }

    public DefaultDownload setDownloadSegment(long start,long end){
        mDownloadSegment=new DownloadSegment(start,end);
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
    public void setChangedFile(File newFile) {
        mTargetFile=newFile;
    }

    @Override
    public File getTargetFile() {
        return mTargetFile;
    }

    @Override
    public DownloadSegment supportSegment() {
        return mDownloadSegment;
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
    public OutputStream getOutputStream() throws IOException {
        if(mDownloadSegment==null) return new FileOutputStream(mTargetFile);
        else if(mDownloadSegment.breakMode()) return new FileOutputStream(mTargetFile,true);
        else return new SegmentOutputStream();
    }

    private class SegmentOutputStream extends OutputStream{
        private RandomAccessFile mRandomAccessFile;

        public SegmentOutputStream() throws IOException {
            mRandomAccessFile=new RandomAccessFile(mTargetFile,"rw");
            mRandomAccessFile.seek(mDownloadSegment.getStart());
        }

        @Override
        public void write(int b) throws IOException {
            mRandomAccessFile.write(b);
        }

        @Override
        public void write(@NonNull byte[] b, int off, int len) throws IOException {
            mRandomAccessFile.write(b,off,len);
        }

        @Override
        public void close() throws IOException {
            super.close();
            mRandomAccessFile.close();
        }
    }
}
