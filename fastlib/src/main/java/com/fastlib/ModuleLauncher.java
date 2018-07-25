package com.fastlib;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexFile;

/**
 * Created by sgfb on 2018/7/24.
 */

public class ModuleLauncher {
    private Map<String, String> mPathMap = new HashMap<>();
    private static ModuleLauncher mInstance;

    private ModuleLauncher() {
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

    /**
     * 请求跳转
     * @param context 上下文
     * @param moduleName 模块名
     */
    public void start(Context context, String moduleName) {
        String modulePath=mPathMap.get(moduleName);

        if(!TextUtils.isEmpty(modulePath)){
            try {
                Intent intent=new Intent(context,Class.forName(modulePath));
                context.startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else System.out.println("no path found!");
    }

    /**
     * 加载dex中class
     * @param path dex加载路径
     * @param outPath dex解压路径
     */
    private void loadDexFile(String path,String outPath){
        try {
            long timer = System.currentTimeMillis();
            DexFile dexFile = DexFile.loadDex(path,outPath, 0);
            Enumeration<String> set = dexFile.entries();

            while (set.hasMoreElements()) {
                String className = set.nextElement();
                if (className.endsWith("Fastlib$Function$ModuleWarehouse")) {
                    Class cla = Class.forName(className);
                    Map<String, String> moduleMap;
                    Field moduleMapField;

                    moduleMapField = cla.getDeclaredField("mPathMap");
                    moduleMapField.setAccessible(true);
                    moduleMap = (Map<String, String>) moduleMapField.get(cla.newInstance());

                    if (moduleMap != null && !moduleMap.isEmpty())
                        mPathMap.putAll(moduleMap);
                }
            }
            dexFile.close();
            System.out.println("load dex file cost:" + (System.currentTimeMillis() - timer) + "ms");
        } catch (IOException | ClassNotFoundException | NoSuchFieldException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
