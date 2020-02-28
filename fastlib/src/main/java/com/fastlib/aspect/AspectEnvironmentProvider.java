package com.fastlib.aspect;

import java.util.List;

/**
 * Created by sgfb on 2020\02\17.
 * 切面事件环境提供器
 */
public interface AspectEnvironmentProvider {

    void addEnvs(Object env);

    List getAspectEnvironment();

    void environmentDestroy();
}
