package com.fastlib.bean;

/**
 * Created by sgfb on 16/10/11.
 * 流量纪录
 */
public class NetFlow{
    public int requestCount;
    public long receiveByte;
    public long takeByte;
    public String time;

    @Override
    public String toString(){
        return "Rx:"+receiveByte+" Tx:"+takeByte+" RequestCount:"+requestCount+" time:"+time;
    }
}