package com.fastlib.url_image.request;

import android.app.Activity;
import android.support.v4.app.Fragment;

import java.io.File;

/**
 * Created by sgfb on 18/1/23.
 * 图像来自磁盘的图像请求
 */
public class DiskBitmapRequest extends BitmapRequest<File>{

    public DiskBitmapRequest(File from, Activity activity) {
        super(from, activity);
    }

    public DiskBitmapRequest(File from, Fragment fragment) {
        super(from, fragment);
    }

    @Override
    public String getKey(){
        return mResource.getAbsolutePath();
    }

    @Override
    public File indicateSaveFile() {
        return mResource;
    }
}