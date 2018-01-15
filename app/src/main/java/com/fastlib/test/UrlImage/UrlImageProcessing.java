package com.fastlib.test.UrlImage;

/**
 * Created by sgfb on 18/1/15.
 * 一个具有状态的网络图像处理
 */
public abstract class UrlImageProcessing implements HostLifecycle{
    protected BitmapRequest mRequest;
    protected ImageDispatchCallback mCallback;

    public UrlImageProcessing(BitmapRequest request, ImageDispatchCallback callback) {
        mRequest = request;
        mCallback = callback;
    }

    public abstract void handle(ImageProcessingManager processingManager);
}