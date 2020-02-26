package com.fastlib.net2.download;

import com.fastlib.annotation.Database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 2020\02\24.
 * 多线程下载.已下载和未下载的块记录
 */
public class MultiDownloadProgress{
    @Database(keyPrimary = true)
    public String filePath;
    public List<MultiDownloadPoint> downloadedSegment;

    public MultiDownloadProgress() {}

    public MultiDownloadProgress(String filePath) {
        this.filePath = filePath;
        downloadedSegment=new ArrayList<>();
    }
}