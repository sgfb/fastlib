package com.fastlib.db;

import android.text.TextUtils;

import com.fastlib.annotation.DatabaseInject;
import com.fastlib.bean.RemoteDataCache;
import com.fastlib.net.Listener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;
import com.fastlib.net.Result;

import java.util.List;

/**
 * 使用这个类时可以不用关心数据是来自哪里(数据库或者网络数据源)，注解uri支持
 *
 * @author sgfb
 *
 **/
public class DataDelegater{
	private FastDatabase mDatabase;
	private OnDataBack mBack;
    private Request mRequest;
	//需要的对象实体
	private Class<?> mCla;

	public DataDelegater(Class<?> cla,Request request,OnDataBack databack){
		mCla=cla;
		mDatabase=FastDatabase.getInstance();
        mBack=databack;
        mRequest=request;

		DatabaseInject inject=mCla.getAnnotation(DatabaseInject.class);
		if(inject!=null&&!TextUtils.isEmpty(inject.remoteUri()))
			request.setUrl(inject.remoteUri());
		else
			throw new UnsupportedOperationException("不支持没有DatabaseInject和remoteUri注解的对象使用DataDelegater");
	}

	/**
	 * 先查看数据库中是否有想要的数据，无论有没有都会向服务器寻求数据
     * 如果有数据将会回调两遍
	 */
	public void start(){
        List<Object> list=FastDatabase.getInstance().get(RemoteDataCache.class, mCla.getName());
        RemoteDataCache cache;

        final Listener l=mRequest.getListener();
        if(list!=null&&list.size()>0) {
            cache = (RemoteDataCache) list.get(0);
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
                RemoteDataCache responseCache=new RemoteDataCache();
                responseCache.setCache(result.getBody());
                responseCache.setCacheName(mCla.getName());
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

    public void refresh(){
        NetQueue.getInstance().netRequest(mRequest);
    }

	public interface OnDataBack{
        void data(Object object);
	}
}
