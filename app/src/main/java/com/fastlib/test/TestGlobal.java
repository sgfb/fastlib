package com.fastlib.test;

import com.fastlib.app.AppGlobal;
import com.fastlib.db.FastDatabase;

/**
 * Created by sgfb on 16/2/12.
 */
public class TestGlobal implements AppGlobal{
    private static TestGlobal mOwer;
    private String rootAddress;

    private TestGlobal() {
        super();
    }

    public static synchronized TestGlobal getInstance(){
        if(mOwer==null)
            mOwer=new TestGlobal();
        return mOwer;
    }

    @Override
    public String getRootAddress() {
        return rootAddress;
    }

    @Override
    public void setRootAddress(String address) {
        rootAddress=address;
    }

    @Override
    public int getDatabaseVersion() {
        return FastDatabase.getInstance().getConfig().getVersion();
    }

    @Override
    public void setDatabaseVersion(int version) {
        FastDatabase.getInstance().getConfig().setVersion(version);
    }
}
