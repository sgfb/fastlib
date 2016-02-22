package com.fastlib.app;

/**
 * 全局属性.应当以单例存在并且被继承以具体化.全局sxing
 */
public abstract class AppGlobal{
    //网络请求根地址
    private String rootAddress;

    protected AppGlobal(){

    }

    public String getRootAddress() {
        return rootAddress;
    }

    public void setRootAddress(String rootAddress) {
        this.rootAddress = rootAddress;
    }
}
