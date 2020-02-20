package com.fastlib.net2;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastlib.db.SaveUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sgfb on 2020\02\20.
 * 单线程,可选覆盖原文件
 */
public class SingleDownloadController extends SimpleDownloadController{

    public SingleDownloadController(@NonNull File targetFile) {
        super(targetFile);
    }

    public SingleDownloadController(@NonNull File targetFile, boolean useServerFilename, boolean append) {
        super(targetFile, useServerFilename, append);
    }

    @Override
    protected void onDownloadReady(File toFile,InputStream inputStream, @Nullable String filename, long length) throws IOException {
        SaveUtil.saveToFile(toFile,inputStream,append);
    }
}
