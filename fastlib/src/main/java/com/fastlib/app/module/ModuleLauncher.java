package com.fastlib.app.module;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dalvik.system.DexFile;

/**
 * Created by sgfb on 2018/7/24.
 * 模块启动器.管理初始化，启动逻辑
 */
public class ModuleLauncher {
    private static ModuleLauncher mInstance;
    private Map<String, String> mPathMap = new HashMap<>();           //module path-->module class name
    private Map<String,List<String>> mModuleGroupMap=new HashMap<>(); //module name-->module groups
    private ModuleLauncherControl mGlobalControl=new ModuleLauncherControlAdapter();
    private ModuleLauncherControl mEmptyControl=new ModuleLauncherControlAdapter();

    private ModuleLauncher(){
    }

    public static ModuleLauncher getInstance() {
        if (mInstance == null) mInstance = new ModuleLauncher();
        return mInstance;
    }

    public void init(Context context) {
        String dexPath = context.getPackageResourcePath();
        File outputFile = new File(context.getCacheDir(), "temp.apk");
        loadDexFile(dexPath,outputFile.getAbsolutePath());
    }

    public void start(Context context,ModuleRequest request){
        start(context,request,-1);
    }

    public void startForResult(Activity activity,ModuleRequest request,int requestCode){
        start(activity,request,requestCode);
    }

    /**
     * 请求跳转
     * @param context 上下文
     * @param request 模块请求
     * @param requestCode 是否支持模块返回参数，-1为不支持
     */
    public void start(Context context,ModuleRequest request,int requestCode){
        ModuleLauncherControl control=request.isPassGlobalControl()?mEmptyControl:mGlobalControl;
        control.initialization(context,request,requestCode);

        String modulePath=mPathMap.get(request.getPath());
        Class moduleCla=request.getModuleClass();

        if(TextUtils.isEmpty(modulePath)&&moduleCla==null){
            List<String> moduleGroup=mModuleGroupMap.get(request.getModuleName());
            if(moduleGroup==null||moduleGroup.isEmpty()){
                System.out.println("no path found!");
                control.error(new IllegalArgumentException("no path found"),request);
            }
            else control.error(new MismatchModuleGroupException(moduleGroup),request);
            return;
        }
        try {
            int containLayoutId=request.getContainLayoutId();

            if(moduleCla==null)
                moduleCla=Class.forName(modulePath);
            if(containLayoutId==-1){
                Intent intent=new Intent(context,moduleCla);
                intent.putExtras(request.getParams());

                if(requestCode!=-1)
                    ((Activity)context).startActivityForResult(intent,requestCode);
                else context.startActivity(intent);
            }
            else{
                Fragment fragment= (Fragment) moduleCla.newInstance();
                AppCompatActivity activity= (AppCompatActivity) context;

                fragment.setArguments(request.getParams());
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(containLayoutId,fragment)
                        .commit();
            }
            control.success(request);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            control.error(e,request);
        } catch (InstantiationException e) {
            e.printStackTrace();
            control.error(e,request);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            control.error(e,request);
        }
    }

    /**
     * 加载dex中class
     * @param path dex加载路径
     * @param outPath dex解压路径
     */
    public void loadDexFile(String path,String outPath){
        try {
            long timer = System.currentTimeMillis();
            DexFile dexFile = DexFile.loadDex(path,outPath, 0);
            Enumeration<String> set = dexFile.entries();

            while (set.hasMoreElements()) {
                String className = set.nextElement();
                if (className.endsWith(CheckModuleProcessor.CLASS_NAME)) {
                    Class cla = Class.forName(className);
                    Map<String, String> moduleMap;
                    Field moduleMapField;
                    Object instance=cla.newInstance();

                    moduleMapField = cla.getDeclaredField("mPathMap");
                    moduleMapField.setAccessible(true);
                    moduleMap = (Map<String, String>) moduleMapField.get(instance);

                    if (moduleMap != null && !moduleMap.isEmpty())
                        mPathMap.putAll(moduleMap);

                    //装载 mModuleGroupMap
                    Field moduleGroupMapField=cla.getDeclaredField("mModuleGroup");
                    moduleGroupMapField.setAccessible(true);
                    Map<String,List<String>> moduleGroupMap= (Map<String, List<String>>) moduleGroupMapField.get(instance);
                    for(Map.Entry<String,List<String>> entry:moduleGroupMap.entrySet()){
                        List<String> list=mModuleGroupMap.get(entry.getKey());

                        if(list==null){
                            list=new ArrayList<>();
                            mModuleGroupMap.put(entry.getKey(),list);
                        }
                        list.addAll(entry.getValue());
                    }
                }
            }
            dexFile.close();
            System.out.println("load dex file cost:" + (System.currentTimeMillis() - timer) + "ms");
        } catch (IOException | ClassNotFoundException | NoSuchFieldException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setGlobalListener(@NonNull ModuleLauncherControl listener){
        mGlobalControl=listener;
    }
}
