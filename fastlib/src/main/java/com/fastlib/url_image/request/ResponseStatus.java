package com.fastlib.url_image.request;

/**
 * Created by Administrator on 2018/5/18.
 * 图像请求返回的某些状态
 */
public class ResponseStatus{
    private boolean isFromMemory;

    public boolean isFromMemory() {
        return isFromMemory;
    }

    public void setFromMemory(boolean fromMemory) {
        isFromMemory = fromMemory;
    }
}