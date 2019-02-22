package com.fastlib.url_image.state;

import android.util.Log;

import com.fastlib.db.SaveUtil;
import com.fastlib.db.MemoryPool;
import com.fastlib.url_image.request.ImageRequest;

import java.io.File;
import java.util.Locale;

/**
 * Created by sgfb on 19/2/13.
 * E-mail: 602687446@qq.com
 * 本地图像加载过程
 */
public class LocalImageLoadMemoryState extends ImageState<File>{

    public LocalImageLoadMemoryState(ImageRequest<File> request){
        super(request);
    }

    @Override
    protected ImageState handle() throws Exception{
        Log.d(TAG,String.format(Locale.getDefault(),"图像加载至内存:%s",mRequest.getSimpleName()));
        MemoryPool.getInstance().putCache(mRequest.getName(), SaveUtil.loadFile(mRequest.getSource().getAbsolutePath()));
        return new LocalImageRenderState(mRequest);
    }
}