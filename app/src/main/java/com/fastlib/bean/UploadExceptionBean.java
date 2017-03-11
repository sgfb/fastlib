package com.fastlib.bean;

import android.os.Build;

import com.fastlib.annotation.Database;

import java.io.Serializable;

/**
 * Created by sgfb on 17/2/16.
 * 上传异常数据实体类
 */
public class UploadExceptionBean implements Serializable{
    @Database(keyPrimary = true,autoincrement = true)
    public long id;
    private int systemVersion=Build.VERSION.SDK_INT; //android版本
    public int appVersion; //app版本
    private String phoneModel= Build.MODEL; //手机具体型号
    public String message; //奔溃原因
    public String causePosition; //异常发生点
    public String netStatus; //网络状态
    public String projectName;

    public int getSystemVersion() {
        return systemVersion;
    }

    public String getPhoneModel() {
        return phoneModel;
    }
}