package com.fastlib.url_image.request;

import android.app.Activity;
import androidx.fragment.app.Fragment;

import com.fastlib.url_image.FastImage;
import com.fastlib.utils.Utils;

import java.io.File;

/**
 * Created by sgfb on 18/1/23.
 * 图像来自远程服务器的图像请求
 */
public class UrlImageRequest extends ImageRequest<String> {

    public UrlImageRequest(String from, Activity activity) {
        super(from, activity);
    }

    public UrlImageRequest(String from, Fragment fragment) {
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