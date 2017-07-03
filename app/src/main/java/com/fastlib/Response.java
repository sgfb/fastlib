package com.fastlib;

/**
 * Created by sgfb on 17/6/28.
 */

public class Response<T>{
    public T data;
    public Status status;

    public static class Status{
        public String message;
        public String code;
        public boolean success;
    }
}
