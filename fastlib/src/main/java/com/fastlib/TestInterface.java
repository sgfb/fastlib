package com.fastlib;

import com.fastlib.net.BaseParam;
import com.fastlib.net.Net;

/**
 * Created by sgfb on 2019\03\10.
 */
@Net
public interface TestInterface{

    @BaseParam(method = "get",url = "/com.ranktech.taoyidai_1.2.11_66.apk",customRootAddress = "https://release-static.rank-tech.com")
    String justTest(int a,String b);
}
