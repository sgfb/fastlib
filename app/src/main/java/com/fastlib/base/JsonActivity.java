package com.fastlib.base;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.fastlib.app.FastActivity;
import com.fastlib.app.GlobalConfig;
import com.fastlib.net.Listener;
import com.fastlib.net.Request;
import com.fastlib.utils.json.FastJson;
import com.fastlib.utils.json.JsonObject;
import com.fastlib.utils.json.JsonViewBinder;

import java.io.IOException;

/**
 * Created by sgfb on 16/9/21.
 * 绑定来自服务器的json数据填充
 */
public abstract class JsonActivity extends FastActivity implements Listener<String>{
    public static final String TAG=JsonActivity.class.getSimpleName();

    private SparseArray<Request> mIdRequestMap; //视图id对网络请求映射
    private SparseArray<View> mIdViewMap; //视图id对视图映射
    protected JsonViewBinder mBinder;

    public abstract SparseArray<Request> generateRequest();
    public abstract void initAfter();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mIdViewMap=new SparseArray<>();
        mIdRequestMap=generateRequest();
        for(int i=0;i<mIdRequestMap.size();i++)
            mIdRequestMap.get(mIdRequestMap.keyAt(i)).setListener(this).setGenericType(String.class);
    }

    @Override
    public void setContentView(int layoutResID){
        super.setContentView(layoutResID);
        mBinder=new JsonViewBinder(this);
        for(int i=0;i<mIdRequestMap.size();i++){
            int key=mIdRequestMap.keyAt(i);
            Request request=mIdRequestMap.get(key);
            net(request);
        }
        initAfter();
    }

    @Override
    public void setContentView(View view){
        super.setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params){
        super.setContentView(view, params);
    }

    @Override
    public void onResponseListener(Request r, String result){
        int key=mIdRequestMap.keyAt(mIdRequestMap.indexOfValue(r));
        View v=getView(key);
        try {
            JsonObject jo=FastJson.fromJson(result);
            mBinder.bindDataToView(v,jo);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    protected  <T extends View> T getView(int id){
        View v=mIdViewMap.get(id);
        if(v==null){
            v=findViewById(id);
            if(v!=null)
                mIdViewMap.put(id,v);
        }
        return (T) v;
    }

    @Override
    public void onErrorListener(Request r, String error){
        if(GlobalConfig.SHOW_LOG)
            Log.d(TAG,"request "+r+" error "+error);
    }
}