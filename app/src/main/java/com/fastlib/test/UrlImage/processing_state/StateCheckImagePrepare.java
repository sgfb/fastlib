package com.fastlib.test.UrlImage.processing_state;

import android.graphics.BitmapFactory;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.fastlib.bean.ImageFileInfo;
import com.fastlib.db.And;
import com.fastlib.db.Condition;
import com.fastlib.db.FastDatabase;
import com.fastlib.test.UrlImage.BitmapWrapper;
import com.fastlib.test.UrlImage.ImageDispatchCallback;
import com.fastlib.test.UrlImage.ImageProcessManager;
import com.fastlib.test.UrlImage.UrlImageProcessing;
import com.fastlib.test.UrlImage.request.BitmapRequest;
import com.fastlib.utils.ScreenUtils;

import java.io.File;

/**
 * Created by sgfb on 18/1/15.
 * 第一次在发起网络请求前往磁盘中取图像（必须是存在的，否则这个状态应该被跳过）
 * 请求完后判断是否有请求对应url图像文件，如果没有则结束流程否则跳到判断资源过期流程
 */
public class StateCheckImagePrepare extends UrlImageProcessing{

    public StateCheckImagePrepare(BitmapRequest request, ImageDispatchCallback callback) {
        super(request, callback);
    }

    @Override
    public void handle(ImageProcessManager processingManager){
        System.out.println("发起网络请求前从磁盘读取图像信息:"+mRequest.getResource());
        if(!checkDownloadComplete()){
            processingManager.imageProcessStateConvert(false,this,new StateDownloadImageIfExpire(mRequest,mCallback));
            return;
        }
        File file=mRequest.getSaveFile();
        BitmapFactory.Options options=new BitmapFactory.Options();
        BitmapFactory.Options justDecodeBoundOptions=new BitmapFactory.Options();

        justDecodeBoundOptions.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(file.getAbsolutePath(),justDecodeBoundOptions);
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
        wrapper.bitmap =BitmapFactory.decodeFile(file.getAbsolutePath(),options);
        mCallback.complete(this,mRequest,wrapper);
        if(!TextUtils.isEmpty((String)mRequest.getResource()))
            processingManager.imageProcessStateConvert(false,this,new StateDownloadImageIfExpire(mRequest,mCallback));
    }

    /**
     * 检查图像下载状态
     * @return true为下载完整，false不完整
     */
    private boolean checkDownloadComplete(){
        ImageFileInfo imageFileInfo= FastDatabase.getDefaultInstance(mRequest.getContext())
                .addFilter(And.condition(Condition.equal(mRequest.getKey())))
                .getFirst(ImageFileInfo.class);
        return imageFileInfo!=null&&imageFileInfo.isDownloadComplete;
    }
}
