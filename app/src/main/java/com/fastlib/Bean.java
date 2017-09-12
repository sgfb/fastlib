package com.fastlib;

/**
 * Created by sgfb on 17/9/11.
 */

public class Bean<T>{
    public T data;
    public Status status;

    @Override
    public String toString() {
        return "Bean{" +
                "data=" + data +
                ", status=" + status +
                '}';
    }
}
