package com.fastlib;

import com.fastlib.db.SaveUtil;
import com.fastlib.utils.Utils;

import java.io.File;
import java.io.IOException;
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
        System.out.println(String.format(Locale.getDefault(),"图像加载至内存:%s",mRequest.getSimpleName()));
        File file=new File(ImageManager.getInstance().getConfig().mSaveFolder, Utils.getMd5(mRequest.mSource,false));
        MemoryPool.getInstance().putCache(mRequest.getName(), SaveUtil.loadFile(file.getAbsolutePath()));
        return new UrlImageRenderState(mRequest);
    }
}
