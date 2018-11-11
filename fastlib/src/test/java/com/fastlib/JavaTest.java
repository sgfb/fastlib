package com.fastlib;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2018/7/27.
 */
@RunWith(JUnit4.class)
public class JavaTest{

    @Test
    public void testMain(){
        String s="\uFEFF{code\":200,\"message\":\"\\u624b\\u673a\\u9a8c\\u8bc1\\u7801\\u5df2\\u53d1\\u9001\\u6210\\u529f\",\"success\":true}";
        try {
            System.out.println(new String(s.getBytes(),"utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}