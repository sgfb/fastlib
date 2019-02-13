package com.fastlib.utils.monitor;

import com.fastlib.bean.event.EventDownloading;
import com.fastlib.bean.event.EventUploading;

public class Requesting{
    public static final int STATUS_WAITING=1;
    public static final int STATUS_REQUESTING=2;
    public static final int STATUS_ERROR=3;
    public static final int STATUS_SUCCESS=4;
    public static final int STATUS_EXCEPTION=5;
    public static final int TYPE_NORMAL=1;
    public static final int TYPE_DOWNLOADING=2;
    public static final int TYPE_UPLOADING=3;

    public int status;
    public int type;
    public long timeConsume;
    public long contentLength;
    public String url;
    public EventDownloading downloading;
    public EventUploading uploading;

    public Requesting(int status, int type, String url) {
        this.status = status;
        this.type = type;
        this.url = url;
    }
}
