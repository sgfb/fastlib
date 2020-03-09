package com.fastlib.url_image.processing;

import android.graphics.BitmapFactory;
import androidx.core.util.Pair;

import com.fastlib.url_image.ImageProcessManager;
import com.fastlib.url_image.bean.BitmapWrapper;
import com.fastlib.url_image.callback.ImageDispatchCallback;
import com.fastlib.url_image.request.ImageRequest;
import com.fastlib.utils.ScreenUtils;

import java.io.File;

/**
 * Created by sgfb on 18/1/15.
 * 从磁盘中读取图像,这是最后一个状态
 */
public class StateLoadNewImageOnDisk extends UrlImageProcessing{

    public StateLoadNewImageOnDisk(ImageRequest request, ImageDispatchCallback callback) {
        super(request, callback);
    }

    @Override
    public void handle(ImageProcessManager processingManager) {
        System.out.println("从磁盘读取图像到内存:"+mRequest.getResource());
        File file=mRequest.getSaveFile();
        BitmapFactory.Options options=new BitmapFactory.Options();
        BitmapFactory.Options justDecodeBoundOptions=new BitmapFactory.Options();

        justDecodeBoundOptions.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(file.getAbsolutePath(),justDecodeBoundOptions);
        //如果请求宽高非0，尝试读取指定宽高中的最低值等比缩小。非0则尝试读取比手机屏幕小的尺寸
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

        wrapper.originWidth=justDecodeBoundOptions.outWidth;
        wrapper.originHeight=justDecodeBoundOptions.outHeight;
        wrapper.sampleSize =options.inSampleSize;
        wrapper.bitmap =BitmapFactory.decodeFile(file.getAbsolutePath(),options);
        mCallback.complete(this,mRequest,wrapper);
        processingManager.getRequestList().remove(mRequest);
    }
}