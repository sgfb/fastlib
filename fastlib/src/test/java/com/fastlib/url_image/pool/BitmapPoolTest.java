package com.fastlib.url_image.pool;

import android.graphics.Bitmap;

import com.fastlib.url_image.bean.BitmapWrapper;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Created by Administrator on 2018/5/17.
 */
@RunWith(RobolectricTestRunner.class)
public class BitmapPoolTest{
    BitmapPool mPool;

    @Before
    public void setUp() throws Exception {
        mPool=new BitmapPool(new TargetReference());
    }

    /**
     * 判断初始化和调整大小后（有图像，无图像），剩余空间是否正常
     */
    @Test
    public void remainTest(){
        long expectSize=30*1024*1024/5;
        Assert.assertEquals(expectSize,mPool.getRemainSize());

        mPool.setPoolSize(expectSize=1024*1024);
        Assert.assertEquals(expectSize,mPool.getRemainSize());

        BitmapWrapper wrapper=new BitmapWrapper();
        wrapper.bitmap= Bitmap.createBitmap(100,100, Bitmap.Config.ARGB_8888);
        mPool.addBitmap("key",wrapper);
        expectSize-=wrapper.bitmap.getByteCount();
        Assert.assertEquals(expectSize,mPool.getRemainSize());

        mPool.setPoolSize(expectSize=1024*1024*10);
        expectSize-=wrapper.bitmap.getByteCount();
        Assert.assertEquals(expectSize,mPool.getRemainSize());

        mPool.setPoolSize(expectSize=wrapper.bitmap.getByteCount()-1);
        Assert.assertEquals(expectSize,mPool.getRemainSize());
    }
}
