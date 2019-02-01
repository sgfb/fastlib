package com.fastlib;

public class CheckState implements ImageState,Runnable{
    private ImageRequest mRequest;

    public CheckState(ImageRequest mRequest) {
        this.mRequest = mRequest;
    }

    @Override
    public void run(){
        handle(mRequest);
    }

    @Override
    public void handle(ImageRequest request){


        CallbackParcel callbackParcel=ImageManager.getInstance().getCallbackParcel();
        if(callbackParcel!=null)
            callbackParcel.success(request,null);
        if(request.mCallbackParcel!=null)
            request.mCallbackParcel.success(request,null);
    }
}
