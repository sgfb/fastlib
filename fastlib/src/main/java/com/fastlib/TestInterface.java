package com.fastlib;

import com.fastlib.net2.utils.RequestTo;

/**
 * Created by sgfb on 2020\03\02.
 */
public interface TestInterface{

    @RequestTo(url = "http://www.baidu.com")
    String visitorBaidu();
}
