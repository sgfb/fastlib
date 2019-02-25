package com.fastlib.image_manager.state;

import android.graphics.BitmapFactory;
import android.util.Log;

import com.fastlib.db.MemoryPool;
import com.fastlib.image_manager.ImageUtils;
import com.fastlib.image_manager.bean.ImageConfig;
import com.fastlib.image_manager.request.ImageRequest;
import com.fastlib.utils.ImageUtil;

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
                mRequest.getCallbackParcel().success(mRequest, ImageUtils.cropBitmap(mRequest.getRequestWidth(),mRequest.getRequestHeight(),mRequest.getBitmapConfig(),data));
            else mRequest.getCallbackParcel().failure(mRequest,new IllegalStateException());
        }
        else if((mRequest.getStoreStrategy()&ImageConfig.STRATEGY_LOAD_DISK_ONLY)!=0){
            mRequest.getCallbackParcel().success(mRequest, ImageUtils.cropBitmap(mRequest.getRequestWidth(),mRequest.getRequestHeight(),mRequest.getBitmapConfig(),mRequest.getSource()));
        }
        return null;
    }
}
