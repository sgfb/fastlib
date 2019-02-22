package com.fastlib.url_image.state;

import android.graphics.BitmapFactory;
import android.util.Log;

import com.fastlib.db.MemoryPool;
import com.fastlib.url_image.bean.ImageConfig;
import com.fastlib.url_image.request.ImageRequest;

import java.io.File;
import java.util.Locale;

/**
 * Created by sgfb on 19/2/13.
 * E-mail: 602687446@qq.com
 */
public class LocalImageRenderState extends ImageState<File>{

    public LocalImageRenderState(ImageRequest<File> request) {
        super(request);
    }

    @Override
    protected ImageState handle(){
        Log.d(TAG,String.format(Locale.getDefault(),"开始渲染:%s",mRequest.getSimpleName()));
        if(mRequest.getCallbackParcel()==null) return null;
        if((mRequest.getStoreStrategy()& ImageConfig.STRATEGY_LOAD_NORMAL)!=0){
            byte[] data= MemoryPool.getInstance().getCache(mRequest.getName());
            if(data!=null)
                mRequest.getCallbackParcel().success(mRequest, BitmapFactory.decodeByteArray(data,0,data.length));
            else mRequest.getCallbackParcel().failure(mRequest,new IllegalStateException());
        }
        else if((mRequest.getStoreStrategy()&ImageConfig.STRATEGY_LOAD_DISK_ONLY)!=0){
            //TODO 裁剪
            mRequest.getCallbackParcel().success(mRequest,BitmapFactory.decodeFile(mRequest.getSource().getAbsolutePath()));
        }
        return null;
    }
}
