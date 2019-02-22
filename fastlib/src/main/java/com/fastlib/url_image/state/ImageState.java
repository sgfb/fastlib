package com.fastlib.url_image.state;

import com.fastlib.url_image.ImageManager;
import com.fastlib.url_image.request.CallbackParcel;
import com.fastlib.url_image.request.ImageRequest;

public abstract class ImageState<T> implements Runnable{
    protected static final String TAG=ImageState.class.getSimpleName();
    protected ImageRequest<T> mRequest;

    public ImageState(ImageRequest<T> request){
        mRequest=request;
        mRequest.setOnCancelListener(new ImageRequest.OnCancelListener() {
            @Override
            public void canceled() {
                onCancel();
            }
        });

        CallbackParcel callbackParcel= ImageManager.getInstance().getCallbackParcel();
        if(callbackParcel!=null&&mRequest.isCanceled()) callbackParcel.failure(mRequest,new IllegalStateException("canceled"));
    }

    /**
     * 状态具体处理
     * @return  下一个状态
     */
    protected abstract ImageState handle()throws Exception;

    protected void onCancel(){}

    @Override
    public void run(){
        CallbackParcel callbackParcel=ImageManager.getInstance().getCallbackParcel();
        try {
            ImageState is = handle();
            if(mRequest.isCanceled()) throw new IllegalStateException("canceled");
            if(is!=null) is.run();
            else{
                if(callbackParcel!=null)
                    callbackParcel.success(mRequest,null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(callbackParcel!=null) callbackParcel.failure(mRequest,e);
            if(mRequest.getCallbackParcel()!=null) mRequest.getCallbackParcel().failure(mRequest,e);
        }
    }
}