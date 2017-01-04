package com.fastlib.db;

import android.content.Context;
import android.support.annotation.Nullable;

import com.fastlib.bean.RemoteCache;
import com.fastlib.net.ExtraListener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by sgfb on 16/12/29.
 * 缓存来自服务器的数据,获取时不用关系数据来自哪里
 */
public class ServerCache{
    private long mCacheTimeLife =0; //缓存生存长度.默认没有生存时间
    private Context mContext;
    private RemoteCache mCache;
    private ThreadPoolExecutor mThreadPool;
    private Request mRequest;
    private String mToDatabase;
    private ExtraListener mOldListener;

    public ServerCache(Request request,String cacheName){
        this(request,cacheName,null,null);
    }

    public ServerCache(Request request,String cacheName,@Nullable String toDatabase){
        this(request,cacheName,toDatabase,null);
    }

    public ServerCache(Request request, String cacheName,@Nullable String toDatabase,ThreadPoolExecutor threadPool){
        mCache=new RemoteCache();
        mThreadPool=threadPool;
        mRequest=request;
        mCache.cacheName=cacheName;
        mToDatabase=toDatabase;
        init();
    }

    private void init(){
        getFastDatabase().get(mCache); //尝试从数据库中获取一下缓存
        mOldListener=mRequest.getListener();
        mRequest.setListener(new ExtraListener() {
            @Override
            public void onRawData(byte[] data){
                if(mOldListener!=null)
                    mOldListener.onRawData(data);
                //暂时不保存来自服务器的二进制数据
            }

            @Override
            public void onTranslateJson(final String json) {
                if(mOldListener!=null)
                    mOldListener.onTranslateJson(json);
                if(mThreadPool!=null)
                    mThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            saveCache(json);
                        }
                    });
                else
                    saveCache(json);
            }

            @Override
            public void onResponseListener(Request r, Object result) {
                if(mOldListener!=null)
                    mOldListener.onResponseListener(r,result);
            }

            @Override
            public void onErrorListener(Request r, String error) {
                if(mOldListener!=null)
                    mOldListener.onErrorListener(r,error);
            }
        });
    }

    /**
     * 重新标上缓存时间,保存缓存
     * @param json
     */
    private void saveCache(String json){
        mCache.expiry=System.currentTimeMillis()+mCacheTimeLife;
        mCache.cache=json;
        getFastDatabase().saveOrUpdate(mCache);
    }

    private FastDatabase getFastDatabase(){
        return mToDatabase==null?new FastDatabase(mContext):new FastDatabase(mContext).toWhichDatabase(mToDatabase);
    }

    /**
     * 刷新缓存,如果不强制刷新则根据生存时间来决定
     * @param force 是否强制刷新
     */
    public void refresh(boolean force){
        if(force||mCache.expiry<System.currentTimeMillis())
            NetQueue.getInstance().netRequest(mThreadPool,mRequest);
        else
            toggleCallback();
    }

    /**
     * 使用缓存触发数据回调
     */
    private void toggleCallback(){
        Gson gson=new Gson();
        mOldListener.onTranslateJson(mCache.cache);
        Type type=mRequest.getGenericType();
        if(type==String.class)
            mOldListener.onResponseListener(mRequest,mCache.cache);
        else if(type!=null&&type!=Object.class)
            mOldListener.onResponseListener(mRequest,gson.fromJson(mCache.cache,type));
    }

    public long getCacheTimeLife() {
        return mCacheTimeLife;
    }

    public void setCacheTimeLife(long cacheTimeLife) {
        mCacheTimeLife = cacheTimeLife;
    }

    public Request getRequest() {
        return mRequest;
    }

    public void setRequest(Request request) {
        mRequest = request;
    }
}
