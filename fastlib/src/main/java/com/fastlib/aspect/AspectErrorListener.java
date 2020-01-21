package com.fastlib.aspect;

/**
 * Created by sgfb on 2020\01\13.
 * 切面逻辑异常或无法或出现其他分支时回调
 */
public interface AspectErrorListener{

    void onAspectError(Exception exception);
}
