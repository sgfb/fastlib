package com.fastlib.app;

import android.app.Application;

import com.fastlib.db.FastDatabase;
import com.fastlib.net.Request;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局环境配置
 */
public class FastApplication extends Application{
    private static FastApplication mApp;
    private Map<String,Request[]> mAllRequest;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp=this;
        FastDatabase.build(this);
        EventObserver2.build(this);
        readRequestPool();
    }

    private void readRequestPool(){
        try {
            InputStream in = getAssets().open("RequestPool");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int len;
            while ((len = in.read(data)) != -1)
                baos.write(data, 0, len);
            in.close();
            String json=baos.toString();
            Gson gson=new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            Type type=new TypeToken<Map<String,Request[]>>(){}.getType();
            mAllRequest=gson.fromJson(json,type);
        } catch (IOException|JsonParseException e){
            //do noting
        }
        if(mAllRequest==null)
            mAllRequest=new HashMap<>(1);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static FastApplication getInstance(){
        return mApp;
    }

    public Request[] getRequestFromModule(String moduleName){
        return mAllRequest.get(moduleName);
    }
}
