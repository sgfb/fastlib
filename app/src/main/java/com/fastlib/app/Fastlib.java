package com.fastlib.app;

import android.content.Context;

/**
 * Created by sgfb on 17/5/11.
 */
public class Fastlib{
    private static boolean sShowLog =true; //是否显示log,包括System.out和Log.x
    private static String sDefaultName ="fastlib"; //默认的Sp名，数据库名

    private Fastlib(){

    }

    public static void init(Context context){
        EventObserver.build(context);
    }

    public void isShowLog(boolean showLog){
        sShowLog =showLog;
    }

    public void setDefaultName(String name){
        sDefaultName =name;
    }

    public static boolean isShowLog() {
        return sShowLog;
    }

    public static String getsDefaultName() {
        return sDefaultName;
    }
}
