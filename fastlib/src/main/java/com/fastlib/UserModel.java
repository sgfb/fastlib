package com.fastlib;

import com.processor.Net;
import com.processor.Url;

/**
 * Created by Administrator on 2018/4/24.
 */
@Net
public interface UserModel{

    @Url("http://www.baidu2222.com")
    String login(String phone,String pwd);
}