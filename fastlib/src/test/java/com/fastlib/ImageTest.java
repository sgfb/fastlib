package com.fastlib;

import android.os.MemoryFile;

import com.fastlib.db.MemoryPool;
import com.fastlib.image_manager.request.CallbackParcel;
import com.fastlib.image_manager.ImageManager;
import com.fastlib.image_manager.request.ImageRequest;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ImageTest{
    private int mTestRunningCount;

    @Before
    public void setUp(){
        mTestRunningCount=0;
    }

    @Test
    public void testAddRequest() throws InterruptedException {

    }

    @Test
    public void testImageMemoryPool(){
        MemoryFile mf=Mockito.mock(MemoryFile.class);
        MemoryPool.getInstance().putCache("a",new byte[10]);

    }
}
