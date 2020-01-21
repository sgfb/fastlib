package com.fastlib;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import com.fastlib.annotation.Permission;
import com.fastlib.aspect.GetImageFromAlbum;
import com.fastlib.aspect.ThreadOn;
import com.fastlib.net2.Request;
import com.fastlib.utils.ImageUtil;

import java.lang.reflect.Method;

/**
 * Created by sgfb on 2020\01\07.
 */
public class MainController {

    @ThreadOn(ThreadOn.ThreadType.WORK)
    public Response<String> getSimpleData() throws NoSuchMethodException {
        Method method=getClass().getDeclaredMethod("getSimpleData");
        return (Response<String>) new Request("http://192.168.3.15:8082/getSimpleData","GET").startSyc(method.getReturnType());
    }

    @ThreadOn(ThreadOn.ThreadType.MAIN)
    public String justDoit(){
        System.out.println(Thread.currentThread().getName());
        return "just do it";
    }

    @ThreadOn(ThreadOn.ThreadType.WORK)
    public void doNow(Context context){
        try{
            doCall(context);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @ThreadOn(ThreadOn.ThreadType.WORK)
    @Permission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void doCall(Context context){
        try{
            ImageUtil.openAlbum((Activity) context);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @ThreadOn(ThreadOn.ThreadType.WORK)
    public void showImageFromAlbum(){
        System.out.println(getImageFromAlbum());
    }

    @GetImageFromAlbum
    private String getImageFromAlbum(){
        return null;
    }
}
