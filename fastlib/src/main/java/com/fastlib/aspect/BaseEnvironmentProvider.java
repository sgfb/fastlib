package com.fastlib.aspect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 2020\02\22.
 */
public abstract class BaseEnvironmentProvider implements AspectEnvironmentProvider{
    private List<Object> mEnvs=new ArrayList<>();

    public void addEnvs(Object env){
        mEnvs.add(env);
    }

    @Override
    public List getAspectEnvironment() {
        return mEnvs;
    }

    @Override
    public void environmentDestroy() {
        mEnvs.clear();
    }
}
