package com.fastlib;

import com.fastlib.image_manager.request.CallbackParcel;
import com.fastlib.image_manager.ImageManager;
import com.fastlib.image_manager.request.ImageRequest;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

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
        int cycCount=100;
        for(int i=0;i<cycCount;i++){
            ImageRequest<String> request=new ImageRequest<String>() {};
            request.mSource ="test"+i;
            request.mCallbackParcel=new CallbackParcel() {
                @Override
                public void prepareLoad(ImageRequest request) {

                }

                @Override
                public void success(ImageRequest request, byte[] data) {
                    mTestRunningCount++;
                }

                @Override
                public void failure(ImageRequest request) {
                    mTestRunningCount++;
                }
            };
            ImageManager.getInstance().addRequest(request);
        }
        Thread.sleep(100);
        Assert.assertEquals(0,ImageManager.getInstance().getNormalList().size());
        Assert.assertEquals(0,ImageManager.getInstance().getPendingList().size());
        Assert.assertEquals(cycCount,mTestRunningCount);
    }
}
