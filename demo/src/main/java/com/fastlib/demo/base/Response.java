package com.fastlib.demo.base;

public class Response<T>{
    public String code;
    public String message;
    public T data;

    public Response(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
