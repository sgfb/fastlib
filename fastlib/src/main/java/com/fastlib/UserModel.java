package com.fastlib;

import com.fastlib.net.Request;
import com.fastlib.net.SimpleListener;
import com.processor.Net;
import com.processor.Url;

/**
 * Created by sgfb on 18/4/22.
 */
@Net
public class UserModel{

    @Url("http://www.baidu.com")
    String login(String name,String phone){
        Request request=new Request("url");
        request.setListener(new SimpleListener<String>(){

            @Override
            public void onResponseListener(Request r, String result) {
                loginCallback(result);
            }
        });
        return null;
    }

    void loginCallback(String result){

    }
}
