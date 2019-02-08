package com.fastlib.net;

import com.fastlib.app.module.ModuleLife;
import com.fastlib.net.listener.SimpleListener;
import com.fastlib.utils.SessionCheck;
import com.fastlib.utils.TestUtil;
import com.google.gson.reflect.TypeToken;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.io.File;

/**
 * Created by sgfb on 18/8/18.
 * 网络请求包裹内部测试
 */
@RunWith(RobolectricTestRunner.class)
public class RequestTest{
    public static final Object sLock=new Object();

//    @Test
//    public void testRemoveParam(){
//        Request request=new Request();
//
//        //测试单项删除
//        request.add("key","value1");
//        request.add("key",1);
//        request.add("key",0.1f);
//        if(request.getParamsRaw().isEmpty()){
//
//        }
//        request.removeParam("key",false);
//    }

    @Test
    public void uploadTest() throws Exception {
        Request request=new Request("http://192.168.0.100:8080/FastProject/UploadFile");
        request.put("file",new File("/Users/sgfb/Downloads/途尔模块.png"));
        TestUtil.netInterfaceCheck(request, new TypeToken<String>() {
        }, new SessionCheck<String>() {
            @Override
            public String check(String entity){
                System.out.println("session:"+entity);
                return null;
            }
        });
    }
}
