package com.fastlib.url_image.state;

import android.util.Log;

import com.fastlib.db.SaveUtil;
import com.fastlib.url_image.ImageManager;
import com.fastlib.db.MemoryPool;
import com.fastlib.url_image.request.ImageRequest;
import com.fastlib.utils.Utils;

import java.io.File;
import java.util.Locale;

/**
 * Created by sgfb on 19/2/12.
 * E-mail: 602687446@qq.com
 * 将图像从外存读入到内存中
 */
public class UrlImageLoadMemoryState extends ImageState<String>{

    UrlImageLoadMemoryState(ImageRequest<String> request){
        super(request);
    }

    @Override
    protected ImageState handle()throws Exception{
        Log.d(TAG,String.format(Locale.getDefault(),"图像加载至内存:%s",mRequest.getSimpleName()));
        File file=new File(ImageManager.getInstance().getConfig().mSaveFolder, Utils.getMd5(mRequest.getSource(),false));
        MemoryPool.getInstance().putCache(mRequest.getName(), SaveUtil.loadFile(file.getAbsolutePath()));
        return new UrlImageRenderState(mRequest);
    }
}
