package com.fastlib.bean;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import com.fastlib.BuildConfig;
import com.fastlib.utils.NetUtils;

/**
 * Created by sgfb on 17/2/16.
 * 默认的奔溃异常数据实体类
 */
public class CrashExceptionBean{
    public final int systemVersion=Build.VERSION.SDK_INT; //android版本
    public final  String phoneModel= Build.MODEL; //手机具体型号

    public int crashLevel=1; //奔溃严重级别,越高越严重
    public int appVersion=BuildConfig.VERSION_CODE; //app版本
    public int totalMemory; //系统内存总量，单位MB
    public int useMemory; //已使用内存，单位MB
    public String message; //奔溃原因
    public String causePosition; //异常发生点
    public String netStatus; //网络状态
    public String projectName= BuildConfig.APPLICATION_ID;
    public String abi;
    public String extra;

    public CrashExceptionBean(){}

    public CrashExceptionBean(Context context){
        baseInfoCollection(context);
    }

    /**
     * 基本信息收集.注意申请权限
     */
    public void baseInfoCollection(Context context){
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo=new ActivityManager.MemoryInfo();

        am.getMemoryInfo(memoryInfo);
        totalMemory= (int) (memoryInfo.totalMem/1024/1024);
        useMemory= (int) ((memoryInfo.totalMem-memoryInfo.availMem)/1024/1024);
        if(NetUtils.isConnected(context))
            netStatus=NetUtils.isWifi(context)?"WIFI":"Remote";
        else netStatus="No connect";
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            String[] abis=Build.SUPPORTED_ABIS;
            StringBuilder sb=new StringBuilder();
            for(String abi:abis)
                sb.append(abi).append(",");
            if(sb.length()>0)
                sb.deleteCharAt(sb.length());
            this.abi=sb.toString();
        }
        else this.abi="ABI:"+ Build.CPU_ABI+" ABI2:"+Build.CPU_ABI2;
    }

    @Override
    public String toString() {
        return "CrashExceptionBean{" +
                "systemVersion=" + systemVersion +
                ", phoneModel='" + phoneModel + '\'' +
                ", crashLevel=" + crashLevel +
                ", appVersion=" + appVersion +
                ", totalMemory=" + totalMemory +
                ", useMemory=" + useMemory +
                ", message='" + message + '\'' +
                ", causePosition='" + causePosition + '\'' +
                ", netStatus='" + netStatus + '\'' +
                ", projectName='" + projectName + '\'' +
                ", extra='" + extra + '\'' +
                '}';
    }
}