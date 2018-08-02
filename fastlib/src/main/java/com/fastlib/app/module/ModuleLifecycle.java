package com.fastlib.app.module;

/**
 * Created by sgfb on 18/7/17.
 * 模块生命周期
 */
public interface ModuleLifecycle {
    void created();

    void destroyed();
}
