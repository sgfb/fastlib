package com.fastlib.url_image.processing;

import android.graphics.BitmapFactory;
import android.support.v4.util.Pair;

import com.fastlib.url_image.ImageProcessManager;
import com.fastlib.url_image.bean.BitmapWrapper;
import com.fastlib.url_image.callback.ImageDispatchCallback;
import com.fastlib.url_image.request.ImageRequest;
import com.fastlib.utils.ScreenUtils;

/**
 * Created by sgfb on 18/1/22.
 * 从内部资源中读取图像
 */
public class StateLoadImageOnResource extends UrlImageProcessing{

    public StateLoadImageOnResource(ImageRequest request, ImageDispatchCallback callback) {
        super(request,callback);
    }

    @Override
    public void handle(ImageProcessManager processingManager){
        System.out.println("发送请求从内部资源中读取图像 ID号:"+mRequest.getResource());

        BitmapFactory.Options options=new BitmapFactory.Options();
        BitmapFactory.Options justDecodeBoundOptions=new BitmapFactory.Options();

        justDecodeBoundOptions.inJustDecodeBounds=true;
        BitmapFactory.decodeResource(mRequest.getContext().getResources(),(int)mRequest.getResource(),justDecodeBoundOptions);
        //如果请求宽高非0,尝试读取指定宽高中的最低值等比缩小.非0则尝试读取比手机屏幕小的尺寸
        if(mRequest.getRequestWidth()!=0&&mRequest.getRequestHeight()!=0){
            float widthRadio=(float)justDecodeBoundOptions.outWidth/(float)mRequest.getRequestWidth();
            float heightRadio=(float)justDecodeBoundOptions.outHeight/(float)mRequest.getRequestHeight();
            int maxRadio= (int) Math.ceil(Math.max(widthRadio,heightRadio));

            if(maxRadio>1)
                options.inSampleSize=maxRadio;
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
        wrapper.bitmap =BitmapFactory.decodeResource(mRequest.getContext().getResources(),(int)mRequest.getResource(),options);
        mCallback.complete(this,mRequest,wrapper);
    }
}