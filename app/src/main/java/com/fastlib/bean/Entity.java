package com.fastlib.bean;

import android.util.Log;

import com.fastlib.annotation.DatabaseInject;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.Serializable;

/**
 * 实体基类,实现了序列化
 * 
 * @author shenhaofeng
 * 
 */
@SuppressWarnings("serial")
public abstract class Entity implements Serializable {
	@DatabaseInject(ignore=true)
	public static final String TAG=Entity.class.getSimpleName();

	@DatabaseInject(keyPrimary = true,autoincrement = true)
	protected int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public  Entity getInstance(){
		return this;
	}

	public String toJson(){
		Gson gson=new Gson();
		return gson.toJson(this);
	}

	public static <T> T fromJson(String json,Class<?> cla){
		Gson gson=new Gson();
		try{
			return (T) gson.fromJson(json,cla);
		}catch(JsonParseException e){
			Log.w(TAG,"JsonParseException:"+e.toString());
		}
		return null;
	}
}
