package com.fastlib.Bean;

import android.text.TextUtils;

import com.fastlib.InterfaceTest;
import com.fastlib.net.Request;
import com.fastlib.net.SimpleMockProcessor;
import com.fastlib.utils.SessionCheck;
import com.fastlib.utils.TestUtil;
import com.google.gson.reflect.TypeToken;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.junit.Assert;

/**
 * Created by sgfb on 18/3/10.
 */

public class InterfaceTestCase extends TestCase{

    public InterfaceTestCase(String name) {
        super(name);
    }

    public static Test getTest(){
        TestSuite ts=new TestSuite();
        ts.addTest(new InterfaceTestCase("testConnectionError"));
        ts.addTest(new InterfaceTestCase("testModelError"));
        ts.addTest(new InterfaceTestCase("testSessionError"));
        ts.addTest(new InterfaceTestCase("testNormalInterface"));
        return ts;
    }

    /**
     * 物理层异常测试
     * @throws Exception
     */
    public void testConnectionError() throws Exception {
        interfaceTestExample("http://192.168.1.1/");
    }

    /**
     * 数据模型异常测试
     * @throws Exception
     */
    public void testModelError() throws Exception {
        interfaceTestExample("http://www.baidu.com");
    }

    /**
     * 模拟业务异常的接口测试
     * @throws Exception
     */
    public void testSessionError() throws Exception {
        interfaceTestExample(new SimpleMockProcessor("{\"id\":-10,\"name\":\"sgfb\"}"));
    }

    /**
     * 模拟正常的接口测试
     * @throws Exception
     */
    public void testNormalInterface() throws Exception {
        interfaceTestExample(new SimpleMockProcessor("{\"id\":10,\"name\":\"sgfb\"}"));
    }

    private void interfaceTestExample(String url)throws Exception{
        interfaceTestExample(url,null);
    }

    private void interfaceTestExample(SimpleMockProcessor mock)throws Exception{
        interfaceTestExample(null,mock);
    }

    private void interfaceTestExample(String url,SimpleMockProcessor mock) throws Exception {
        String testResult = TestUtil.netInterfaceCheck(mock==null?new Request("get", url):new Request(mock), new TypeToken<Bean>() {
        }, new SessionCheck<Bean>() {
            @Override
            public String check(Bean entity) {
                return entity.id > 1 ? null : "业务层错误";
            }
        });
        Assert.assertTrue(testResult,TextUtils.isEmpty(testResult));
    }
}