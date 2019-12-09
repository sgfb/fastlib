package com.fastlib;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

/**
 * Created by sgfb on 2019/12/9
 * E-mail:602687446@qq.com
 * Socket留存池.当双方Header中没有指明Connection为close并且连接还未断开可重复使用连接的流
 */
public class SocketEntityPool{
    private static SocketEntityPool mInstance;
    private Map<String,List<SocketEntity>> mEntityMap=new HashMap<>();

    private SocketEntityPool(){}

    public static SocketEntityPool getInstance(){
        if(mInstance==null){
            synchronized (SocketEntityPool.class){
                mInstance=new SocketEntityPool();
            }
        }
        return mInstance;
    }

    /**
     * 从池中取一个socket实体(可能是新创建的)
     * @param url   统一资源定位符
     * @return  socket实体
     */
    public @Nullable SocketEntity getSocketEntity(String url) throws IOException {
        String key=URLUtil.getHostAndPort(url);
        List<SocketEntity> list=mEntityMap.get(key);

        if(list==null){
            list=new ArrayList<>();
            mEntityMap.put(key,list);
        }
        while(!list.isEmpty()){
            SocketEntity entity=list.remove(0);
            if(entity.isValid()) return entity;
            else entity.close();
        }
        return new SocketEntity(url,!url.startsWith("https") ? new Socket(): SSLSocketFactory.getDefault().createSocket());
    }

    /**
     * 返回一个实体到池中
     * @param entity    socket实体
     */
    public void returnSocketEntity(SocketEntity entity) throws IOException {
        if(entity.isValid()){
            String key=URLUtil.getHostAndPort(entity.getUrl());
            List<SocketEntity> list=mEntityMap.get(key);

            if(list==null){
                list=new ArrayList<>();
                mEntityMap.put(key,list);
            }
            list.add(entity);
        }
        else entity.close();
    }
}