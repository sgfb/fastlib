package com.fastlib.url_image.state;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.Pair;
import android.util.Log;

import com.fastlib.url_image.ImageManager;
import com.fastlib.db.MemoryPool;
import com.fastlib.url_image.bean.ImageConfig;
import com.fastlib.url_image.request.ImageRequest;
import com.fastlib.utils.ScreenUtils;
import com.fastlib.utils.Utils;

import java.io.File;
import java.util.Locale;

/**
 * Created by sgfb on 19/2/12.
 * E-mail: 602687446@qq.com
 * 远程图像渲染过程
 */
public class UrlImageRenderState extends ImageState<String>{

    public UrlImageRenderState(ImageRequest<String> request) {
        super(request);
    }

    @Override
    protected ImageState handle(){
        Log.d(TAG,String.format(Locale.getDefault(),"开始渲染:%s",mRequest.getSimpleName()));
        if(mRequest.getCallbackParcel()==null) return null;

        if((mRequest.getStoreStrategy()&ImageConfig.STRATEGY_LOAD_NORMAL)!=0){
            byte[] data= MemoryPool.getInstance().getCache(mRequest.getName());
            if(data!=null)
            mRequest.getCallbackParcel().success(mRequest,computeFitBitmap(null,data));
            else mRequest.getCallbackParcel().failure(mRequest,new IllegalStateException());
        }
        else if((mRequest.getStoreStrategy()&ImageConfig.STRATEGY_LOAD_DISK_ONLY)!=0){
            File file=new File(ImageManager.getInstance().getConfig().mSaveFolder, Utils.getMd5(mRequest.getSource(),false));
            mRequest.getCallbackParcel().success(mRequest,computeFitBitmap(file,null));
        }
        return null;
    }

    @SuppressWarnings("all")
    private Bitmap computeFitBitmap(File file, byte[] data){
        BitmapFactory.Options options=new BitmapFactory.Options();
        BitmapFactory.Options justDecodeBoundOptions=new BitmapFactory.Options();

        justDecodeBoundOptions.inJustDecodeBounds=true;
        if(file!=null) BitmapFactory.decodeFile(file.getAbsolutePath(),justDecodeBoundOptions);
        else BitmapFactory.decodeByteArray(data,0,data.length,justDecodeBoundOptions);
        //如果请求宽高非0，尝试读取指定宽高中的最低值等比缩小.都为0则尝试读取比手机屏幕小的尺寸
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
        if(options.inSampleSize>1)
        Log.d(TAG,String.format(Locale.getDefault(),"裁切:(%s,%s)-->(%s,%s)",justDecodeBoundOptions.outWidth, justDecodeBoundOptions.outHeight,
                justDecodeBoundOptions.outWidth/options.inSampleSize,justDecodeBoundOptions.outHeight/options.inSampleSize));
        if(file!=null) return BitmapFactory.decodeFile(file.getAbsolutePath(),options);
        else return BitmapFactory.decodeByteArray(data,0,data.length,options);
    }
}
