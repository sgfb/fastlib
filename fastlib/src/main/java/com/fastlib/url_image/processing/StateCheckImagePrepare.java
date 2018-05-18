package com.fastlib.url_image.processing;

import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.fastlib.bean.ImageFileInfo;
import com.fastlib.db.And;
import com.fastlib.db.Condition;
import com.fastlib.db.FastDatabase;
import com.fastlib.url_image.ImageProcessManager;
import com.fastlib.url_image.Target;
import com.fastlib.url_image.bean.BitmapWrapper;
import com.fastlib.url_image.callback.ImageDispatchCallback;
import com.fastlib.url_image.request.ImageRequest;

import java.io.File;

/**
 * Created by sgfb on 18/1/15.
 * 第一次在发起网络请求前往预处理图像
 * 如果有本地图像则调入如果没有置ImageView中Bitmap为placeholder或者null
 * 请求完后判断是否有请求对应url图像文件，如果没有则结束流程否则跳到判断资源过期流程
 */
public class StateCheckImagePrepare extends UrlImageProcessing{

    public StateCheckImagePrepare(ImageRequest request, ImageDispatchCallback callback) {
        super(request, callback);
    }

    @Override
    public void handle(ImageProcessManager processingManager){
        System.out.println("发起网络请求前预处理图像信息:"+mRequest.getResource());
        File file=mRequest.getSaveFile();
        Target target=mRequest.getTarget();
        if(!file.exists()||file.length()<=0||!checkDownloadComplete()){
            if(target!=null)
                target.prepareLoad(mRequest);
            processingManager.imageProcessStateConvert(false,this,new StateDownloadImageIfExpire(mRequest,mCallback));
            return;
        }
        BitmapFactory.Options options=new BitmapFactory.Options();
        BitmapFactory.Options justDecodeBoundOptions=new BitmapFactory.Options();

        justDecodeBoundOptions.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(file.getAbsolutePath(),justDecodeBoundOptions);
        //如果请求宽高非0,尝试读取指定宽高中的最低值等比缩小.非0则尝试读取比手机屏幕小的尺寸
        options.inSampleSize=BitmapWrapper.getLocalImageScale(mRequest.getRequestWidth(),mRequest.getRequestHeight(),
                justDecodeBoundOptions.outWidth,justDecodeBoundOptions.outHeight);
        options.inPreferredConfig=mRequest.getBitmapConfig();
        BitmapWrapper wrapper=new BitmapWrapper();

        wrapper.originWidth=justDecodeBoundOptions.outWidth;
        wrapper.originHeight=justDecodeBoundOptions.outHeight;
        wrapper.sampleSize =options.inSampleSize;
        wrapper.bitmap =BitmapFactory.decodeFile(file.getAbsolutePath(),options);
        if(wrapper.bitmap==null){ //如果本地图像加载失败尝试重新下载
            if(target!=null)
                target.prepareLoad(mRequest);
            processingManager.imageProcessStateConvert(false,this,new StateDownloadImageIfExpire(mRequest,mCallback));
            return;
        }
//        mCallback.complete(this,mRequest,wrapper);
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
        return imageFileInfo==null||imageFileInfo.isDownloadComplete;
    }
}