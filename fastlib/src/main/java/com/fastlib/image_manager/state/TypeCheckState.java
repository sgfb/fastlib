package com.fastlib.image_manager.state;

import com.fastlib.image_manager.ImageManager;
import com.fastlib.image_manager.exception.DuplicateLoadException;
import com.fastlib.image_manager.request.CallbackParcel;
import com.fastlib.image_manager.request.ImageRequest;
import com.fastlib.image_manager.state.ImageState;
import com.fastlib.image_manager.state.LocalImageCheckState;
import com.fastlib.image_manager.exception.UndefineSourceException;
import com.fastlib.image_manager.state.UrlImageCheckState;

import java.io.File;

/**
 * 预检查图像状态后检查图像类型，进入具体类型状态
 */
public class TypeCheckState extends ImageState {

    public TypeCheckState(ImageRequest request) {
        super(request);
    }

    @Override
    protected ImageState handle(){
        if(!preCheck()){
            if(ImageManager.getInstance().getCallbackParcel()!=null)
                ImageManager.getInstance().getCallbackParcel().failure(mRequest,new DuplicateLoadException());
            return null;
        }
        Object resource=mRequest.getSource();
        if(resource instanceof String)
            return new UrlImageCheckState(mRequest);
        if(resource instanceof File)
            return new LocalImageCheckState(mRequest);
        if(resource instanceof Integer){
            //TODO
        }
        CallbackParcel callbackParcel= ImageManager.getInstance().getCallbackParcel();
        if(callbackParcel!=null) callbackParcel.failure(mRequest,new UndefineSourceException());
        return null;
    }

    /**
     * 预检查
     * @return true预检查正常 进入正常流程 false预检查不正常，不适合进入正常流程
     */
    private boolean preCheck(){
        return true;
    }
}
