package com.fastlib.db;

/**
 * Created by sgfb on 17/1/5.
 */

public interface DatabaseListener<T>{

    void onResult(T data);
}