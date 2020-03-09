package com.fastlib.url_image.request;

import android.app.Activity;
import androidx.fragment.app.Fragment;

import java.io.File;

/**
 * Created by sgfb on 18/1/23.
 * 图像来自磁盘的图像请求
 */
public class DiskImageRequest extends ImageRequest<File> {

    public DiskImageRequest(File from, Activity activity) {
        super(from, activity);
    }

    public DiskImageRequest(File from, Fragment fragment) {
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