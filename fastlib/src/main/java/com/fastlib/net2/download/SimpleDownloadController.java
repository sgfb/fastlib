package com.fastlib.net2.download;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * Created by sgfb on 2020\02\20.
 * 简单下载控制实现
 * 如果使用断点续传请注意本类未检查文件是否过期
 */
public abstract class SimpleDownloadController implements DownloadStreamController{
    private boolean useServerFilename;
    protected boolean supportAppend;    //是否支持断点续传
    private File mTargetFile;
    private File mRealDownloadedFile;

    protected abstract void onDownloadReady(File toFile,InputStream inputStream, @Nullable String filename, long length) throws IOException;

    public SimpleDownloadController(@NonNull File targetFile) {
        this(targetFile,false,false);
    }

    public SimpleDownloadController(@NonNull File targetFile,boolean useServerFilename, boolean append) {
        this.useServerFilename = useServerFilename;
        this.supportAppend = append;
        this.mTargetFile = targetFile;
    }

    @Override
    public void onStreamReady(InputStream inputStream, @Nullable String filename, long length) throws IOException {
        if(mTargetFile.exists()){
            if(mTargetFile.isDirectory())
                mRealDownloadedFile=createRandomFile(mTargetFile);
            else if(mTargetFile.isFile())
                mRealDownloadedFile=mTargetFile;
        }
        else{
            if(!mTargetFile.createNewFile())
                throw new IOException("文件创建失败");
            mRealDownloadedFile=mTargetFile;
        }
        if(useServerFilename&&!TextUtils.isEmpty(filename)){
            File realFile=new File(mRealDownloadedFile.getParent(),filename);
            mRealDownloadedFile.renameTo(realFile);
        }
        onDownloadReady(mRealDownloadedFile,inputStream,filename,length);
    }

    /**
     * 创建一个随机文件
     * @param parent 父路径
     * @return 格式为:downloadFile_yyyyMMddHHmmss_(自动增加序号)
     */
    private File createRandomFile(File parent) throws IOException {
        String randomName=randomFileName();

        File file;
        int index=1;
        do{
            file=new File(parent,randomName+"_"+Integer.toString(index++));
        }while(file.exists()||file.isDirectory());
        if(!file.createNewFile()) throw new IOException("文件创建失败");
        return file;
    }

    /**
     * 随机生成一个名称
     * @return 格式为:downloadFile_yyyyMMddHHmmss
     */
    private String randomFileName(){
        Calendar calendar=Calendar.getInstance();
        return "downloadFile"+calendar.get(Calendar.YEAR)+
                Integer.toString(calendar.get(Calendar.MONTH)+1)+
                calendar.get(Calendar.DAY_OF_MONTH)+
                calendar.get(Calendar.HOUR)+
                calendar.get(Calendar.MINUTE)+
                calendar.get(Calendar.SECOND);
    }

    @Override
    public File getOutputFile() {
        return mRealDownloadedFile;
    }
}
