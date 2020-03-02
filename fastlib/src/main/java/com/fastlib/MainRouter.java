package com.fastlib;

import com.fastlib.utils.router.ActivityPath;
import com.fastlib.utils.router.Arg;

/**
 * Created by sgfb on 2020\03\01.
 */
public interface MainRouter{

    @ActivityPath(SecondActivity.class)
    void startSecondActivity();

    @ActivityPath(SecondActivity.class)
    void startSecondActivityWithParam(@Arg(SecondActivity.ARG_INT_NUM) int num,
                                      @Arg(SecondActivity.ARG_STR_NAME) String name);
}
