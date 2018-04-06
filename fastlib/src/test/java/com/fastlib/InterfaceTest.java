package com.fastlib;

import junit.framework.TestFailure;
import junit.framework.TestResult;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Enumeration;

/**
 * Created by sgfb on 18/3/9.
 * 接口测试案例
 */
@RunWith(RobolectricTestRunner.class)
public class InterfaceTest{

    @Test
    public void test(){
        TestResult tr=new TestResult();
        InterfaceTestCase.getTest().run(tr);
        Enumeration<TestFailure> errors=tr.errors();
        while(errors.hasMoreElements())
            errors.nextElement().thrownException().printStackTrace();
        Assert.assertTrue(tr.wasSuccessful());
    }
}
