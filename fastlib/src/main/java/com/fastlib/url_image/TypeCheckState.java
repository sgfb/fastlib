package com.fastlib.url_image;

import com.fastlib.url_image.request.CallbackParcel;
import com.fastlib.url_image.request.ImageRequest;
import com.fastlib.url_image.state.ImageState;
import com.fastlib.url_image.state.LocalImageCheckState;
import com.fastlib.url_image.exception.UndefineSourceException;
import com.fastlib.url_image.state.UrlImageCheckState;

import java.io.File;

/**
 * 检查图像类型，进入具体类型状态
 */
public class TypeCheckState extends ImageState {

    public TypeCheckState(ImageRequest request) {
        super(request);
    }

    @Override
    protected ImageState handle(){
        Object resource=mRequest.getSource();
        if(resource instanceof String)
            return new UrlImageCheckState(mRequest);
        if(resource instanceof File)
            return new LocalImageCheckState(mRequest);
        if(resource instanceof Integer){
            //TODO
        }
        CallbackParcel callbackParcel=ImageManager.getInstance().getCallbackParcel();
        if(callbackParcel!=null) callbackParcel.failure(mRequest,new UndefineSourceException());
        return null;
    }
}
