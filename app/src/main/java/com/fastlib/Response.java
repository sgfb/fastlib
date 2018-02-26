package com.fastlib;

/**
 * Created by Administrator on 2018/2/6.
 */
public class Response<T>{
    public int code;
    public String message;
    public boolean success;
    public T data;
}
