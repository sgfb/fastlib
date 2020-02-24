package com.fastlib;

import com.fastlib.aspect.BaseEnvironmentProvider;
import com.fastlib.aspect.ThreadOn;
import com.fastlib.aspect.component.inject.Cache;
import com.fastlib.aspect.component.inject.GetImageFromAlbum;
import com.fastlib.aspect.component.inject.GetImageFromCamera;
import com.fastlib.aspect.component.inject.Logcat;
import com.fastlib.aspect.component.inject.SycStartActivityForResult;

/**
 * Created by sgfb on 2020\01\07.
 */
public class MainController extends BaseEnvironmentProvider {
    int count=0;

    @ThreadOn(ThreadOn.ThreadType.WORK)
    public void testGetImage(){
        System.out.println("image from album:"+getImageFromAlbum());
    }

    @Logcat
    public void justCallNone(){
        System.out.println(none());
    }

    @Logcat
    @GetImageFromAlbum
    public String getImageFromAlbum(){
        return null;
    }

    @Logcat
    public String none(){
        return "none";
    }
}
