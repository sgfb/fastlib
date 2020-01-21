package com.fastlib;

import com.fastlib.net2.Listener;
import com.fastlib.net2.Request;

/**
 * Created by sgfb on 2020\01\11.
 */
public interface TestInterface{

    @RequestTo(url = "http://192.168.3.20:8082/getSimpleData")
    Response<String> justTest(Request request);
}
