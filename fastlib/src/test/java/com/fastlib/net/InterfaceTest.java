package com.fastlib.net;

import com.fastlib.bean.InterfaceCheckTestBean;
import com.fastlib.net.mock.SimpleMockProcessor;
import com.fastlib.utils.SessionCheck;
import com.fastlib.utils.TestUtil;
import com.google.gson.reflect.TypeToken;

import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.net.ConnectException;
import java.util.Enumeration;

/**
 * Created by sgfb on 18/3/9.
 * 接口测试案例
 */
@RunWith(RobolectricTestRunner.class)
public class InterfaceTest{
    public static final String ERROR_SESSION="业务层错误";


    /**
     * 物理层异常测试
     * @throws Exception
     */
    @Test
    public void testConnectionError() throws Exception{
        try{
            String result=interfaceTestExample("http://192.168.1.1/");
            if(!TestUtil.ERROR_HARD_LAYER.equals(result)) throw new Exception("其他错误");
        }catch (ConnectException e){
            String message=e.toString().toLowerCase();
            if(!message.contains("timed out")&&!message.contains("time out")) throw  e;
        }
    }

    /**
     * 数据模型异常测试
     * @throws Exception
     */
    @Test
    public void testModelError() throws Exception {
        String result=interfaceTestExample("http://www.baidu.com");
        if(!TestUtil.ERROR_MODEL_UNMATCH.equals(result)) throw new Exception("其他异常");
    }

    /**
     * 模拟业务异常的接口测试
     * @throws Exception
     */
    @Test
    public void testSessionError() throws Exception {
        String result=interfaceTestExample(new SimpleMockProcessor("{\"id\":-10,\"name\":\"sgfb\"}"));
        if(!ERROR_SESSION.contains(result)) throw new Exception("其他异常");
    }

    /**
     * 模拟正常的接口测试
     * @throws Exception
     */
    @Test
    public void testNormalInterface() throws Exception {
        interfaceTestExample(new SimpleMockProcessor("{\"id\":10,\"name\":\"sgfb\"}"));
    }

    private String interfaceTestExample(String url)throws Exception{
        return interfaceTestExample(url,null);
    }

    private String interfaceTestExample(SimpleMockProcessor mock)throws Exception{
        interfaceTestExample(null,mock);
        return TestUtil.netInterfaceCheck(new Request("http://www.baidu.com"), new TypeToken<InterfaceCheckTestBean>() {
        },new SessionCheck<InterfaceCheckTestBean>() {
            @Override
            public String check(InterfaceCheckTestBean entity) {
                //自定义业务逻辑检查
                return null;
            }
        });
    }

    private String interfaceTestExample(String url,SimpleMockProcessor mock) throws Exception {
        return TestUtil.netInterfaceCheck(mock==null?new Request("get", url):new Request(mock), new TypeToken<InterfaceCheckTestBean>() {
        }, new SessionCheck<InterfaceCheckTestBean>() {
            @Override
            public String check(InterfaceCheckTestBean entity) {
                return entity.id > 1 ? null : ERROR_SESSION;
            }
        });
    }
}