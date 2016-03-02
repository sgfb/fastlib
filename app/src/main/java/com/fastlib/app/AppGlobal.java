package com.fastlib.app;

/**
 * 全局属性
 */
public interface AppGlobal{

    String getRootAddress();
    void setRootAddress(String address);
    int getDatabaseVersion();
    void setDatabaseVersion(int version);
}
