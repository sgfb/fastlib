package com.fastlib;

/**
 * Created by sgfb on 17/7/10.
 */
public class ResponseBean{
    public boolean success;
    public String message;
    public String code;

    @Override
    public String toString() {
        return "ResponseBean{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}