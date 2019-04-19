package com.fastlib;

import com.fastlib.net.BaseParam;
import com.fastlib.net.Net;

/**
 * Create by sgfb on 2019/04/19
 * E-Mail:602687446@qq.com
 */
@Net
public interface CommonInterface {

    @BaseParam(url = "baidu.com",customRootAddress = "http://www.")
    String getBaidu();
}
