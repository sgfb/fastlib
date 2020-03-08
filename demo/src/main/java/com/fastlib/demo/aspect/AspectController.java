package com.fastlib.demo.aspect;

import android.Manifest;

import com.fastlib.annotation.Permission;
import com.fastlib.aspect.ThreadOn;
import com.fastlib.aspect.component.inject.SycStartActivityForResult;

/**
 * Created by sgfb on 2020\03\05.
 */
public class AspectController{

    @ThreadOn(ThreadOn.ThreadType.WORK)
    public String startSecondActivityResult(){
        String str=getSecondActivityResult();
        if(str!=null) str=str+"!";
        return str;
    }

    @SycStartActivityForResult(value = SecondActivity.class,resultKey = SecondActivity.RES_STR_TEST)
    protected String getSecondActivityResult(){
        return null;
    }

    @Permission(Manifest.permission.CAMERA)
    public void getCameraPermission(){

    }
}
