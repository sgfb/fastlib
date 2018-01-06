package com.fastlib.test.UrlImage;

import com.fastlib.net.DefaultDownload;
import com.fastlib.net.Request;
import com.fastlib.net.SimpleListener;

import java.io.File;

/**
 * Created by sgfb on 2017/11/4.
 * Url Image具体请求处理.在子线程中处理
 */
public class UrlImageProcess implements Runnable{
    private BitmapRequest mRequest;
    private ImageDispatchCallback mCallback;

    public UrlImageProcess(BitmapRequest request, ImageDispatchCallback callback) {
        mRequest = request;
        mCallback = callback;
    }

    @Override
    public void run(){

    }
}