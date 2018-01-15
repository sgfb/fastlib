package com.fastlib.test.UrlImage.processing_state;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.util.Pair;

import com.fastlib.test.UrlImage.BitmapRequest;
import com.fastlib.test.UrlImage.BitmapWrapper;
import com.fastlib.test.UrlImage.ImageDispatchCallback;
import com.fastlib.test.UrlImage.ImageProcessingManager;
import com.fastlib.test.UrlImage.UrlImageProcessing;
import com.fastlib.utils.ScreenUtils;

import java.io.File;

/**
 * Created by sgfb on 18/1/15.
 * 第一次在发起网络请求前往磁盘中取图像（必须是存在的，否则这个状态应该被跳过）
 */
public class StateFirstLoadImageOnDisk extends UrlImageProcessing {

    public StateFirstLoadImageOnDisk(BitmapRequest request, ImageDispatchCallback callback) {
        super(request, callback);
    }

    @Override
    public void handle(ImageProcessingManager processingManager){
        System.out.println("发起请求前从磁盘读取图像到内存:"+mRequest.getUrl());
        File file=BitmapRequest.getSaveFile(mRequest);
        BitmapFactory.Options options=new BitmapFactory.Options();
        BitmapFactory.Options justDecodeBoundOptions=new BitmapFactory.Options();

        justDecodeBoundOptions.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(file.getAbsolutePath(),justDecodeBoundOptions);
        //如果请求宽高非0,尝试读取指定宽高中的最低值等比缩小.非0则尝试读取比手机屏幕小的尺寸
        if(mRequest.getRequestWidth()!=0&&mRequest.getRequestHeight()!=0){
            float widthRadio=justDecodeBoundOptions.outWidth/mRequest.getRequestWidth();
            float heightRadio=justDecodeBoundOptions.outHeight/mRequest.getRequestHeight();
            float maxRadio=Math.max(widthRadio,heightRadio);

            if(maxRadio>1)
                options.inSampleSize= (int) maxRadio;
        }
        else{
            Pair<Integer,Integer> screenSize= ScreenUtils.getScreenSize();

            if(justDecodeBoundOptions.outWidth>screenSize.first||justDecodeBoundOptions.outHeight>screenSize.second){
                float widthRadio=justDecodeBoundOptions.outWidth/screenSize.first;
                float heightRadio=justDecodeBoundOptions.outHeight/screenSize.second;
                float maxRadio=Math.max(widthRadio,heightRadio);

                options.inSampleSize= (int) maxRadio;
            }
        }
        options.inPreferredConfig=mRequest.getBitmapConfig();
        BitmapWrapper wrapper=new BitmapWrapper();

        wrapper.mSampleSize=options.inSampleSize;
        wrapper.mBitmap=BitmapFactory.decodeFile(file.getAbsolutePath(),options);
        mCallback.complete(mRequest,wrapper);
        processingManager.imageProcessStateConvert(false,new StateDownloadImageIfExpire(mRequest,mCallback));
    }

    @Override
    public void onStart(Context context){

    }

    @Override
    public void onPause(Context context) {

    }

    @Override
    public void onDestroy(Context context) {

    }
}
