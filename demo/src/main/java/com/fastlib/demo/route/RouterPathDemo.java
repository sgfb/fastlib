package com.fastlib.demo.route;

import com.fastlib.utils.router.ActivityPath;
import com.fastlib.utils.router.Arg;

/**
 * Created by sgfb on 2020\03\01.
 */
public interface RouterPathDemo {

    @ActivityPath(RouteDemoSecondActivity.class)
    void startSecondActivity();

    @ActivityPath(RouteDemoSecondActivity.class)
    void startSecondActivityWithParam(@Arg(RouteDemoSecondActivity.ARG_STR_TEXT) String text);
}
