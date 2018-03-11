package com.fastlib;

import android.test.suitebuilder.TestSuiteBuilder;
import android.text.TextUtils;

import com.fastlib.Bean.Bean;
import com.fastlib.Bean.InterfaceTestCase;
import com.fastlib.net.Request;
import com.fastlib.net.SimpleMockProcessor;
import com.fastlib.utils.SessionCheck;
import com.fastlib.utils.TestUtil;
import com.google.gson.reflect.TypeToken;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
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
