package com.fastlib.net2.download;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sgfb on 2020\02\20.
 * 下载行为控制
 */
public interface DownloadStreamController{

    /**
     * 输入流已开始,这个流不需要执行close,这个方法执行在工作线程中
     * @param inputStream 应当保存为文件的输入流
     * @param filename    取自头部content-disposition中filename段
     * @param length      取自头部content-length
     */
    void onStreamReady(InputStream inputStream, @Nullable String filename, long length) throws IOException;

    /**
     * 下载数据输入到目标文件
     */
    File getOutputFile();
}
