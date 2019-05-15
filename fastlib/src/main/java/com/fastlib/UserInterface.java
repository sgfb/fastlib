package com.fastlib;

import com.fastlib.net.FinalParam;
import com.fastlib.net.Net;

/**
 * Create by sgfb on 2019/05/06
 * E-Mail:602687446@qq.com
 */
@Net
public interface UserInterface{

    @FinalParam({"id","10"})
    String getString();
}
