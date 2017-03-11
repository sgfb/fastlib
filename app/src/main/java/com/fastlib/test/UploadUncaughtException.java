package com.fastlib.test;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;

import com.fastlib.bean.UploadExceptionBean;
import com.fastlib.db.FastDatabase;
import com.fastlib.db.SaveUtil;
import com.fastlib.net.Listener;
import com.fastlib.net.Request;
import com.fastlib.utils.AppInformation;
import com.fastlib.utils.NetUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by sgfb on 17/2/16.
 * 上传异常封装类.异常发生时应该记录到本地，在一段时间间隔后触发上传（如果没有异常则不上传）?
 */
public class UploadUncaughtException implements Thread.UncaughtExceptionHandler{
    private Context mContext;

    public UploadUncaughtException(Context context,Thread thread){
        mContext = context;
        thread.setUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable ex){
        UploadExceptionBean bean=new UploadExceptionBean();
        bean.appVersion= AppInformation.getVersionCode(mContext);
        bean.causePosition=mContext instanceof Activity?((Activity)mContext).getLocalClassName():"top";
        bean.message=ex.getMessage();
        bean.projectName="fastlib";
        if(NetUtils.isConnected(mContext)) bean.netStatus=NetUtils.isWifi(mContext)?"WIFI":"2G/3G/4G";
        else bean.netStatus="none";
        File f=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"track.txt");
        try {
            f.createNewFile();
            SaveUtil.saveToFile(f,bean);
        } catch (IOException e){
            e.printStackTrace();
        }
//        FastDatabase.getDefaultInstance(mContext).saveOrUpdate(bean);
        System.exit(0);
    }

    public static void uploadException(UploadExceptionBean bean){
        Request r=new Request("http://192.168.131.114:8084/FastProject/Test");
        r.put("appVersion",bean.appVersion)
                .put("systemVersion","android"+bean.getSystemVersion())
                .put("phoneModel",bean.getPhoneModel())
                .put("causePosition",bean.causePosition)
                .put("message",bean.message)
                .put("projectName",bean.projectName)
                .start();
    }
}