package com.fastlib.demo.net;

/**
 * Created by sgfb on 2020\03\06.
 */
public class ResponseUploadInfo {
    public long size;
    public String id;
    public String MD5;

    @Override
    public String toString() {
        return "ResponseUploadInfo{" +
                "size=" + size +
                ", id='" + id + '\'' +
                ", MD5='" + MD5 + '\'' +
                '}';
    }
}
