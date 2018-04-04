package com.fastlib.test.UrlImage.request;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.fastlib.test.UrlImage.FastImage;
import com.fastlib.utils.Utils;

import java.io.File;

/**
 * Created by sgfb on 18/1/23.
 * 图像来自远程服务器的图像请求
 */
public class UrlBitmapRequest extends BitmapRequest<String>{

    public UrlBitmapRequest(String from, Activity activity) {
        super(from, activity);
    }

    public UrlBitmapRequest(String from, Fragment fragment) {
        super(from, fragment);
    }

    @Override
    public String getKey() {
        return Utils.getMd5(mResource,false);
    }

    @Override
    public File indicateSaveFile() {
        return new File(FastImage.getConfig().mSaveFolder,getKey());
    }
}