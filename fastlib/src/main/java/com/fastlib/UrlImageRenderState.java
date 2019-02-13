package com.fastlib;

import android.graphics.BitmapFactory;

import com.fastlib.url_image.bean.ImageConfig;
import com.fastlib.utils.Utils;

import java.io.File;
import java.util.Locale;

/**
 * Created by sgfb on 19/2/12.
 * E-mail: 602687446@qq.com
 * 远程图像渲染过程
 */
public class UrlImageRenderState extends ImageState<String>{

    public UrlImageRenderState(ImageRequest<String> request) {
        super(request);
    }

    @Override
    protected ImageState handle(){
        System.out.println(String.format(Locale.getDefault(),"开始渲染:%s",mRequest.getSimpleName()));
        if(mRequest.mCallbackParcel==null) return null;
        if((mRequest.mStoreStrategy&ImageConfig.STRATEGY_LOAD_NORMAL)!=0){
            byte[] data=MemoryPool.getInstance().getCache(mRequest.getName());
            if(data!=null)
            mRequest.mCallbackParcel.success(mRequest,BitmapFactory.decodeByteArray(data,0,data.length));
            else mRequest.mCallbackParcel.failure(mRequest,new IllegalStateException());
        }
        else if((mRequest.mStoreStrategy&ImageConfig.STRATEGY_LOAD_DISK_ONLY)!=0){
            //TODO 裁剪
            File file=new File(ImageManager.getInstance().getConfig().mSaveFolder, Utils.getMd5(mRequest.mSource,false));
            mRequest.mCallbackParcel.success(mRequest,BitmapFactory.decodeFile(file.getAbsolutePath()));
        }
        return null;
    }
}
