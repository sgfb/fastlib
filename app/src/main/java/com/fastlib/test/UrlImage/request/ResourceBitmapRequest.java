package com.fastlib.test.UrlImage.request;

import android.app.Activity;
import android.support.v4.app.Fragment;

import java.io.File;

/**
 * Created by sgfb on 18/1/23.
 * 来自资源库的图像请求
 */
public class ResourceBitmapRequest extends BitmapRequest<Integer>{

    public ResourceBitmapRequest(Integer from, Activity activity) {
        super(from, activity);
    }

    public ResourceBitmapRequest(Integer from, Fragment fragment) {
        super(from, fragment);
    }

    @Override
    public String getKey() {
        return Integer.toString(mResource);
    }

    @Override
    public File indicateSaveFile() {
        return null;
    }
}
