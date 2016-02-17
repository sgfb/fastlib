package com.fastlib.bean;

import com.fastlib.annotation.DatabaseInject;

import java.io.Serializable;

/**
 * 实体基类,实现了序列化
 * 
 * @author shenhaofeng
 * 
 */
@SuppressWarnings("serial")
public class Entity implements Serializable {

	@DatabaseInject(keyPrimary = true,autoincrement = true)
	protected int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
