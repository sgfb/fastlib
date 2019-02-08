package com.fastlib;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/27.
 */
@RunWith(JUnit4.class)
public class JavaTest{

    @Test
    public void testMain(){
        MyTest myTest=new MyTest();
        myTest.mStrList.add("hello");

        try {
            Field field=myTest.getClass().getDeclaredField("mStrList");
            List<String> reflectListStr= (List<String>) field.get(myTest);
            reflectListStr.add("world");
            System.out.println("location");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public class MyTest{
        public List<String> mStrList=new ArrayList<>();
    }
}