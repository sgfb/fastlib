package com.fastlib.net2;

import android.support.annotation.NonNull;

import java.io.File;

/**
 * Created by sgfb on 2020\01\02.
 * 下载模式配置
 */
public class Downloadable {
    private File mTargetFile;
    private boolean isSupportBreak;

    public Downloadable(File targetFile) {
        this(targetFile,false);
    }

    public Downloadable(@NonNull File mTargetFile, boolean isSupportBreak) {
        this.mTargetFile = mTargetFile;
        this.isSupportBreak = isSupportBreak;
    }

    /**
     * 如果为文件夹则下载到此文件夹中（如果能读取到服务器文件名则使用这个文件名否则使用随机名）.如果为空或者文件则保存为此文件
     * @return 下载指定文件或文件夹
     */
    public File getTargetFile() {
        return mTargetFile;
    }

    /**
     * 支持中断
     * @return true支持 false不支持
     */
    public boolean supportBreak() {
        return isSupportBreak;
    }
}
