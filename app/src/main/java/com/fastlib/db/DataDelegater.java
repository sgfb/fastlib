package com.fastlib.db;

import android.text.TextUtils;
import android.util.Log;

import com.fastlib.annotation.DatabaseInject;
import com.fastlib.bean.RemoteDataCache;
import com.fastlib.net.Listener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;
import com.fastlib.net.Result;

import java.util.List;
import java.util.Map;

/**
 * 缓存来自服务器中的数据。使用这个类时可以不用关心数据是来自哪里(数据库或者网络数据源),需要注解uri支持
 *
 * @author sgfb
 *
 **/
public class DataDelegater{
    public static final String TAG=DataDelegater.class.getSimpleName();

    private Request mRequest;
	//需要的对象实体
	private Class<?> mCla;
    private String mStartKey;
    private int mLoadLimit;
    private boolean started;
    private boolean loadMore=false;
    private Map<String,String> mParams;

	public DataDelegater(Class<?> cla,Listener l){
		this(cla, null, l);
	}

    public DataDelegater(Class<?> cla,Map<String,String> params,Listener l){
        mCla=cla;
        mRequest=new Request();

        mRequest.setListener(l);
        DatabaseInject inject=mCla.getAnnotation(DatabaseInject.class);
        if(inject!=null&&!TextUtils.isEmpty(inject.remoteUri()))
            mRequest.setUrl(inject.remoteUri());
        else
            throw new UnsupportedOperationException("不支持没有DatabaseInject和remoteUri注解的对象使用DataDelegater");
        mRequest.setParams(params);
        mParams=params;
    }

	/**
	 * 先查看数据库中是否有想要的数据，无论有没有都会向服务器寻求数据
     * 如果有数据将会回调两遍
	 */
	public void start(){
        if(started) {
            refresh();
            return;
        }
        started=true;
        List<RemoteDataCache> list=FastDatabase.getInstance().get(RemoteDataCache.class, mCla.getName());
        RemoteDataCache cache;

        final Listener l=mRequest.getListener();
        if(list!=null&&list.size()>0) {
            cache = list.get(0);
            Result result=new Result();
            result.setSuccess(true);
            result.setMessage("from database");
            result.setCode(0);
            result.setBody(cache.getCache());
            l.onResponseListener(result);
        }
        mRequest.setListener(new Listener() {

            @Override
            public void onResponseListener(Result result) {
                RemoteDataCache responseCache = new RemoteDataCache();
                responseCache.setCache(result.getBody());
                responseCache.setCacheName(mCla.getName());
                if(!loadMore)
                    FastDatabase.getInstance().saveOrUpdate(responseCache);
                l.onResponseListener(result);
            }

            @Override
            public void onErrorListener(String error) {
                l.onErrorListener(error);
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
        Map<String,String> params=mRequest.getParame();
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
        mRequest.getParame().putAll(params);
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
}
