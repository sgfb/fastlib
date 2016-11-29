package com.fastlib.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;

/**
 * Created by sgfb on 16/4/23.
 */
public class SaveUtil{
    public static final String TAG=SaveUtil.class.getSimpleName();
    public static String sSpName="default"; //存入SharedPreferences时的默认名


    private SaveUtil(){
        //can't instance
    }

    /**
     * 创建临时文件夹中的分类文件夹
     * @param context 上下文
     * @param type 取Environment中文件夹类型
     * @return
     */
    public static File getExternalTempFolder(Context context,String type){
        File root=context.getExternalCacheDir();
        File folder=new File(root,type);
        if(!folder.exists())
            folder.mkdir();
        return folder;
    }

    public static void saveToSp(Context context,String key,Object obj){
        saveToSp(context,sSpName,key,obj);
    }

    /**
     * 保存数据到SharedPreferences.仅支持基本数据
     * @param context
     * @param name
     * @param key
     * @param obj
     */
    public static void saveToSp(Context context,String name,String key,Object obj){
        SharedPreferences sp=context.getSharedPreferences(name,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        if(obj instanceof String)
            editor.putString(key, (String) obj);
        else if(obj instanceof Integer)
            editor.putInt(key,(int)obj);
        else if(obj instanceof Long)
            editor.putLong(key,(long)obj);
        else if(obj instanceof Float)
            editor.putFloat(key,(float)obj);
        else if(obj instanceof Double)
            editor.putFloat(key,(float)obj);
        else if(obj instanceof Boolean)
            editor.putBoolean(key,(boolean)obj);
        else
            Log.w(TAG,"can't recognised the obj type");
        editor.apply();
    }

    /**
     * 从SharedPreferences中取出数据
     * @param context
     * @param name
     * @param key
     * @return
     */
    public static Object getFromSp(Context context,String name,String key){
        SharedPreferences sp=context.getSharedPreferences(name,Context.MODE_PRIVATE);
        return sp.getAll().get(key);
    }

    public static Object getFromSp(Context context,String name,String key,Object def){
        Object obj=getFromSp(context,name,key);
        if(obj==null)
            obj=def;
        return obj;
    }

    /**
     * 存储数据到指定文件
     * @param file
     * @param obj
     * @throws IOException
     */
    public static void saveToFile(File file,Object obj) throws IOException {
        ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(file));
        out.writeObject(obj);
        out.close();
    }

    /**
     * 从指定文件中取出数据
     * @param file
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object getFromFile(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream in=new ObjectInputStream(new FileInputStream(file));
        Object obj=in.readObject();
        in.close();
        return obj;
    }

    /**
     * 保存数据到内部
     * @param context 上下文
     * @param name 包含后缀名
     * @param obj 数据
     * @param isCache 是否缓存
     * @throws IOException
     */
    public static void saveToInternal(Context context,String name,Object obj,boolean isCache) throws IOException {
        File directory=isCache?context.getCacheDir():context.getFilesDir();
        File file=new File(directory+File.separator+name);
        file.createNewFile();
        if(!file.exists()){
            Log.w(TAG,"文件创建失败");
            return;
        }
        saveToFile(file, obj);
    }

    /**
     * 计算缓存占用容量(内部加外部)
     * @param context
     * @return
     */
    public static long cacheSize(Context context){
        File internalDir=context.getCacheDir();
        File externalDir=context.getExternalCacheDir();
        return fileSize(internalDir)+fileSize(externalDir);
    }

    /**
     * 文件或文件夹占用容量
     * @param file
     * @return
     */
    public static long fileSize(File file){
        long count=0;
        if(file.isFile())
            count=file.length();
        else{
            File[] files=file.listFiles();
            for(File f:files)
                count+=fileSize(f);
        }
        return count;
    }

    /**
     * 清理缓存(内部加外部)<br/>
     * 如果缓存放在其他位置,请使用clearFile(File file)
     * @param context
     */
    public static void clearCache(Context context){
        File internalDir=context.getCacheDir();
        File externalDir=context.getExternalCacheDir();
        clearFile(internalDir);
        clearFile(externalDir);
    }

    /**
     * 清理文件,如果是文件夹必须递归删除成空文件夹
     * @param file
     * @return
     */
    public static boolean clearFile(File file){
        if(file.isFile())
            return file.delete();
        else{
            File[] files=file.listFiles();
            for(File f:files){
                boolean b=clearFile(f);
                if(!b)
                    return b;
            }
        }
        return true;
    }
}
