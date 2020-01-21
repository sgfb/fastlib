package com.fastlib;

/**
 * Created by sgfb on 2020\01\09.
 */
public class Response<T> {
    public String message;
    public String code;
    public T data;

    @Override
    public String toString() {
        return "Response{" +
                "message='" + message + '\'' +
                ", code='" + code + '\'' +
                ", data=" + data +
                '}';
    }
}
