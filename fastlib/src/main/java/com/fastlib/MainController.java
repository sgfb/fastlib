package com.fastlib;

import com.fastlib.aspect.BaseEnvironmentProvider;
import com.fastlib.aspect.component.inject.GetImageFromAlbum;
import com.fastlib.aspect.component.inject.GetImageFromCamera;
import com.fastlib.aspect.component.inject.Logcat;
import com.fastlib.aspect.component.inject.SycStartActivityForResult;

/**
 * Created by sgfb on 2020\01\07.
 */
public class MainController extends BaseEnvironmentProvider {

    @GetImageFromCamera
    public String getImageFromCamera(){
        return null;
    }

    @Logcat
    @GetImageFromAlbum
    public String getImageFromAlbum(){
        return null;
    }

    @Logcat
    @SycStartActivityForResult(value = SecondActivity.class,resultKey = "name")
    public String startSecondActivityWaitResult(){
        return null;
    }

    @Logcat
    @SycStartActivityForResult(value = ThirdActivity.class,resultKey = "name")
    public String startThirdActivityWaitResult(){
        return null;
    }
}
