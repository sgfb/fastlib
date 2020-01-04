package com.fastlib.net2;

import com.fastlib.net2.param.RequestParam;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;

/**
 * Created by sgfb on 2019\12\19.
 * 参数解析器测试
 */
@RunWith(RobolectricTestRunner.class)
public class ParamParserTest{
    private static final String KEY="key";
    RequestParam mRequestParam;

    @Before
    public void setUp() throws Exception {
        mRequestParam=new RequestParam();
    }

    @Test
    public void putInt(){
        final int expect=1;
        mRequestParam.add(KEY,expect);
        Assert.assertEquals(expect,Integer.parseInt(mRequestParam.getSurfaceParam().get(KEY).get(0)));

        mRequestParam.put(KEY,expect);
        Assert.assertEquals(expect,Integer.parseInt(mRequestParam.getSurfaceParam().get(KEY).get(0)));
    }

    @Test
    public void putIntWrapper(){
        final Integer expect=2;
        mRequestParam.add(KEY,expect);
        Assert.assertEquals((int)expect,Integer.parseInt(mRequestParam.getSurfaceParam().get(KEY).get(0)));

        mRequestParam.put(KEY,expect);
        Assert.assertEquals((int)expect,Integer.parseInt(mRequestParam.getSurfaceParam().get(KEY).get(0)));
    }

    @Test
    public void putLong(){
        final long expect=1;
        mRequestParam.add(KEY,expect);
        Assert.assertEquals(expect,Long.parseLong(mRequestParam.getSurfaceParam().get(KEY).get(0)));

        mRequestParam.put(KEY,expect);
        Assert.assertEquals(expect,Long.parseLong(mRequestParam.getSurfaceParam().get(KEY).get(0)));
    }

    @Test
    public void putLongWrapper(){
        final Long expect=2L;
        mRequestParam.add(KEY,expect);
        Assert.assertEquals((long)expect,Long.parseLong(mRequestParam.getSurfaceParam().get(KEY).get(0)));

        mRequestParam.put(KEY,expect);
        Assert.assertEquals((long)expect,Long.parseLong(mRequestParam.getSurfaceParam().get(KEY).get(0)));
    }

    @Test
    public void putFloat(){
        final float expect=1.1f;
        mRequestParam.add(KEY,expect);
        Assert.assertEquals(expect,Float.parseFloat(mRequestParam.getSurfaceParam().get(KEY).get(0)));

        mRequestParam.put(KEY,expect);
        Assert.assertEquals(expect,Float.parseFloat(mRequestParam.getSurfaceParam().get(KEY).get(0)));
    }

    @Test
    public void putFloatWrapper(){
        final Float expect=1.2f;
        mRequestParam.add(KEY,expect);
        Assert.assertEquals(expect,Float.parseFloat(mRequestParam.getSurfaceParam().get(KEY).get(0)));

        mRequestParam.put(KEY,expect);
        Assert.assertEquals(expect,Float.parseFloat(mRequestParam.getSurfaceParam().get(KEY).get(0)));
    }

    @Test
    public void putDouble(){
        final double expect=1.1;
        mRequestParam.add(KEY,expect);
        Assert.assertEquals(expect,Double.parseDouble(mRequestParam.getSurfaceParam().get(KEY).get(0)));

        mRequestParam.put(KEY,expect);
        Assert.assertEquals(expect,Double.parseDouble(mRequestParam.getSurfaceParam().get(KEY).get(0)));
    }

    @Test
    public void putDoubleWrapper(){
        final Double expect=1.2;
        mRequestParam.add(KEY,expect);
        Assert.assertEquals(expect,Double.parseDouble(mRequestParam.getSurfaceParam().get(KEY).get(0)));

        mRequestParam.put(KEY,expect);
        Assert.assertEquals(expect,Double.parseDouble(mRequestParam.getSurfaceParam().get(KEY).get(0)));
    }

    @Test
    public void putBoolean(){
        final boolean expect=true;
        mRequestParam.add(KEY,expect);
        Assert.assertEquals(expect,Boolean.parseBoolean(mRequestParam.getSurfaceParam().get(KEY).get(0)));

        mRequestParam.put(KEY,expect);
        Assert.assertEquals(expect,Boolean.parseBoolean(mRequestParam.getSurfaceParam().get(KEY).get(0)));
    }

    @Test
    public void putBooleanWrapper(){
        final Boolean expect=false;
        mRequestParam.add(KEY,expect);
        Assert.assertEquals((boolean)expect,Boolean.parseBoolean(mRequestParam.getSurfaceParam().get(KEY).get(0)));

        mRequestParam.put(KEY,expect);
        Assert.assertEquals((boolean)expect,Boolean.parseBoolean(mRequestParam.getSurfaceParam().get(KEY).get(0)));
    }

    @Test
    public void putFile(){
        final File expect=new File("a");
        mRequestParam.add(KEY,expect);
        Assert.assertEquals(expect,mRequestParam.getBottomParam().get(0).second);
    }
}
