package com.fastlib.app.module;

import android.content.Context;

/**
 * Created by sgfb on 2018/7/25.
 * 模块启动控制器.对模块启动器{@link ModuleLauncher}全局监听并且修改控制参数.其中{@link #error(Exception)}和{@link #success(ModuleRequest)}
 * 必定会回调一个
 */
public interface ModuleLauncherControl{

    /**
     * 在所有逻辑的最前面的一次回调
     * @param context 上下文
     * @param request 模块请求
     * @param requestCode 模块返回请求码
     */
    void initialization(Context context, ModuleRequest request, int requestCode);

    /**
     * 成功启动跳转后回调,这里的启动仅引导成功,并非跳转模块成功
     * @param request 模块请求
     */
    void success(ModuleRequest request);

    /**
     * 路径不正确，类转换失败等错误回调
     * @param e 异常
     */
    void error(Exception e,ModuleRequest request);
}
