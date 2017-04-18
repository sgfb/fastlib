package com.fastlib.app;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.fastlib.net.Listener;
import com.fastlib.net.Request;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by sgfb on 17/2/16.
 * 上传异常封装类.异常发生时应该记录到本地.以天为单位生成日志
 * 这个方法里的异常应该都忽视掉
 */
public abstract class UploadUncaughtException implements Thread.UncaughtExceptionHandler{
    private File mParent;
    private Request mRequest;
    private boolean isMainThread;

    public abstract Request generateRequest(); //生成上传请求

    /**
     * 写异常到异常文件中,这个方法是不保证回调的
     * @param file 存储异常的文件
     * @param ex 异常信息
     */
    public abstract void writeExceptionToFile(@NonNull File file,@NonNull Throwable ex);

    /**
     * 生成要发送的错误参数(键值对)
     * @param errorFile 存储的错误信息的文件
     * @return 要发生至服务器的键值对
     */
    public abstract Pair<String,String> generateErrorParams(File errorFile);

    /**
     * 上传异常类构造
     * @param thread 主线程
     * @param parent 存储父路径
     * @param  isMainThread 是否主线程
     */
    public UploadUncaughtException(Thread thread,File parent,boolean isMainThread){
        mParent=parent;
        this.isMainThread=isMainThread;
        thread.setUncaughtExceptionHandler(this);
        mRequest=generateRequest();
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable ex){
        handleError(ex);
        mRequest.clear();
        mRequest=null;
        if(isMainThread)
            System.exit(0);
    }

    /**
     * 处理错误
     * @param ex
     */
    private void handleError(Throwable ex){
        File f=new File(mParent,generateErrorLogName());
        boolean isExists=true;

        if(!f.exists())
            try {
                isExists=f.createNewFile();
            } catch (IOException e) {
                //因为已经奔溃了，奔溃里的错误什么事都不做
                isExists=false;
            }
        if(isExists)
            writeExceptionToFile(f,ex);
    }

    /**
     * 生成日志名 年.月.日
     * @return 日志名
     */
    private String generateErrorLogName(){
        Calendar calendar=Calendar.getInstance();
        return  "log" +
                calendar.get(Calendar.YEAR) +"_"+
                (calendar.get(Calendar.MONTH) + 1) +"_"+
                calendar.get(Calendar.DAY_OF_MONTH)+
                ".txt";
    }

    /**
     * 检查非今日的数据异常日志，如果存在则上传
     */
    public void uploadHistoryLog(){
        String todayErrorName=generateErrorLogName();
        final Listener listener=mRequest.getListener();

        for(final File logFile:mParent.listFiles()){
            if(logFile.getName().equals(todayErrorName))
                continue;
            Request request=Request.obtain(mRequest.getMethod(),mRequest.getUrl());
            Pair<String,String> param=generateErrorParams(logFile);

            request.put(param.first,param.second);
            request.setListener(new Listener<String>(){

                @Override
                public void onResponseListener(Request r, String result){
                    logFile.delete();
                    if(listener!=null)
                        listener.onResponseListener(r,null);
                    r.clear();
                }

                @Override
                public void onErrorListener(Request r, String error){
                    //因为已经奔溃了，奔溃里的错误什么事都不做
                    if(listener!=null)
                        listener.onErrorListener(r,error);
                    r.clear();
                }
            });
            request.start();
        }
    }
}