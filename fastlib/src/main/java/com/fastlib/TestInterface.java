package com.fastlib;

import com.fastlib.net.BaseParam;
import com.fastlib.net.Net;
import com.fastlib.net.NetMock;

/**
 * Created by sgfb on 2018/7/26.
 */
@Net
public interface TestInterface{

    @BaseParam(url = "http://www.baidu.com",method = "post")
    @NetMock("com.fastlib.TestMock")
    String login(String userName,String password);
}
