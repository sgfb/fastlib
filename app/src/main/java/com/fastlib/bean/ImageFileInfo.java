package com.fastlib.bean;

import com.fastlib.annotation.Database;

/**
 * Created by sgfb on 18/1/24.
 * 对应磁盘上图像信息
 */
public class ImageFileInfo{
    @Database(keyPrimary = true)
    public String key;
    public String lastModified;
    public boolean isDownloadComplete;
}