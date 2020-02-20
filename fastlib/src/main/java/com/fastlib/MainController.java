package com.fastlib;

import com.fastlib.aspect.AspectEnvironmentProvider;
import com.fastlib.aspect.ThreadOn;
import com.fastlib.aspect.component.GetImageFromAlbum;
import com.fastlib.aspect.component.GetImageFromCamera;
import com.fastlib.aspect.component.SycStartActivityForResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 2020\01\07.
 */
public class MainController implements AspectEnvironmentProvider {
    private List mEnvs=new ArrayList();

    @GetImageFromCamera
    public String getImageFromCamera(){
        return null;
    }

    @GetImageFromAlbum
    public String getImageFromAlbum(){
        return null;
    }

    @SycStartActivityForResult(value = MainActivity.class,resultKey = MainActivity.RES_STR_NAME)
    public String startMainActivityWaitResult(){
        return null;
    }

    @Override
    public List getAspectEnvironment() {
        return mEnvs;
    }

    @SuppressWarnings("unchecked")
    public void addEnvs(Object env){
        mEnvs.add(env);
    }
}
