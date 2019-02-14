package com.fastlib.url_image.state;

import com.fastlib.db.MemoryPool;
import com.fastlib.url_image.request.ImageRequest;

import java.io.File;

/**
 * Created by sgfb on 19/2/13.
 * E-mail: 602687446@qq.com
 * 本地图像预处理过程
 */
public class LocalImageCheckState extends ImageState<File>{

    public LocalImageCheckState(ImageRequest<File> request) {
        super(request);
    }

    @Override
    protected ImageState handle() throws Exception{
        if(MemoryPool.getInstance().cacheExists(mRequest.getName()))
            return new LocalImageRenderState(mRequest);
        return new LocalImageLoadMemoryState(mRequest);
    }
}
