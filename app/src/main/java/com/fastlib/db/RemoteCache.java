package com.fastlib.db;

import android.text.TextUtils;
import android.util.Log;

import com.fastlib.net.Listener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;

import java.util.List;
import java.util.Map;

/**
 * 缓存来自服务器中的数据。使用这个类时可以不用关心数据是来自哪里(数据库或者网络数据源)
 *
 * @author sgfb
 **/
public class RemoteCache {
    public static final String TAG=RemoteCache.class.getSimpleName();

    private Request mRequest;
    private String mSourceDatabase; //与哪个数据库交互,如果是null与默认数据库交互
    private String mCacheName;
    private String mStartKey;
    private int mLoadLimit;
    private boolean started;
    private boolean loadMore=false;
    private Map<String,String> mParams;

    public RemoteCache(String url, Listener l){
        this(url,null,l);
    }

    public RemoteCache(String url, Map<String, String> params, Listener l){
        this(url,url,params,l);
    }

    public RemoteCache(String url, String cacheName, Map<String, String> params, Listener l){
        this(url,null,cacheName,params,l);
    }

    public RemoteCache(String url,String sourceDatabase,String cacheName, Map<String, String> params, Listener l){
        mRequest=new Request();

        mRequest.setUrl(url);
        mRequest.setParams(params);
        mRequest.setListener(l);
        mParams=params;
        mCacheName=cacheName;
        mSourceDatabase=sourceDatabase;
    }

    public RemoteCache(Request request){
        this(request.getUrl(),request);
    }

    public RemoteCache(String cacheName, Request request){
        if(TextUtils.isEmpty(request.getUrl()))
            throw new UnsupportedOperationException("不支持没有url的缓存请求");
        mRequest=request;
        mCacheName=cacheName;
        mParams=mRequest.getParams();
    }

    /**
     * 先查看数据库中是否有想要的数据，无论有没有都会向服务器寻求数据</br>
     * 如果有数据将会回调两遍
     */
    public void start(){
        if(started){
            refresh();
            return;
        }
        started=true;
        final FastDatabase database=TextUtils.isEmpty(mSourceDatabase)?FastDatabase.getDefaultInstance():FastDatabase.getDefaultInstance().toWhichDatabase(mSourceDatabase);
        List<com.fastlib.bean.RemoteCache> list=database.get(com.fastlib.bean.RemoteCache.class, mCacheName);
        com.fastlib.bean.RemoteCache cache;
        final Listener l=mRequest.getListener();
        if(list!=null&&list.size()>0) {
            cache = list.get(0);
            l.onResponseListener(mRequest,cache.getCache());
        }
        mRequest.setListener(new Listener(){

            @Override
            public void onResponseListener(Request r,String result) {
                com.fastlib.bean.RemoteCache responseCache=new com.fastlib.bean.RemoteCache();
                responseCache.setCache(result);
                responseCache.setCacheName(mCacheName);
                if(!loadMore) database.saveOrUpdate(responseCache);
                if(l!=null) l.onResponseListener(r,result);
            }

            @Override
            public void onErrorListener(Request r,String error){
                if(l!=null) l.onErrorListener(r,error);
            }
        });
        NetQueue.getInstance().netRequest(mRequest);
    }

    /**
     * 读取更多数据，调用这个方法的时候数据不保存到数据库
     */
    public void loadMore(){
        if(TextUtils.isEmpty(mStartKey)) {
            Log.w(TAG,"没有设置StartKey无法读取更多");
            return;
        }
        loadMore=true;
        Map<String,String> params=mRequest.getParams();
        int start=Integer.parseInt(params.get(mStartKey));
        start+=mLoadLimit;
        params.put(mStartKey,Integer.toString(start));
        NetQueue.getInstance().netRequest(mRequest);
    }

    /**
     * 跳跃读取更多数据，调用这个方法的时候不保存到数据库
     * @param params
     */
    public void loadMore(Map<String,String> params){
        loadMore=true;
        if(mRequest.getParams()==null) mRequest.put(params);
        else mRequest.getParams().putAll(params);
        NetQueue.getInstance().netRequest(mRequest);
    }

    public void setLoadMoreParams(String start,int limit){
        mStartKey=start;
        mLoadLimit=limit;
    }

    private void refresh(){
        loadMore=false;
        mRequest.setParams(mParams);
        NetQueue.getInstance().netRequest(mRequest);
    }

    public Request getRequest(){
        return mRequest;
    }

    public void setSourceDatabase(String database){
        mSourceDatabase=database;
    }
}