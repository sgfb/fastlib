package com.fastlib;

import java.io.File;

/**
 * 检查图像类型，进入具体类型状态
 */
public class TypeCheckState extends ImageState{

    public TypeCheckState(ImageRequest request) {
        super(request);
    }

    @Override
    protected ImageState handle(){
        Object resource=mRequest.mSource;
        if(resource instanceof String)
            return new UrlImageCheckState(mRequest);
        if(resource instanceof File){
            //TODO
        }
        if(resource instanceof Integer){
            //TODO
        }
        CallbackParcel callbackParcel=ImageManager.getInstance().getCallbackParcel();
        if(callbackParcel!=null) callbackParcel.failure(mRequest,new UndefineSourceException());
        return null;
    }
}
