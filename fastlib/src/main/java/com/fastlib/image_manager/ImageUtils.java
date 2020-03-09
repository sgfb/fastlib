package com.fastlib.image_manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.core.util.Pair;
import android.util.Log;

import com.fastlib.utils.ScreenUtils;

import java.io.File;
import java.util.Locale;

/**
 * Created by sgfb on 2019\02\24.
 * 远程图像工具
 */
public class ImageUtils {
    public static final String TAG= ImageUtils.class.getSimpleName();

    private ImageUtils(){
        //工具类，不实例化
    }

    /**
     * 重载{@link #cropBitmap(int, int, Bitmap.Config, File, byte[])}
     */
    public static Bitmap cropBitmap(int width,int height,Bitmap.Config bitmapConfig,File file){
        return cropBitmap(width,height,bitmapConfig,file,null);
    }

    /**
     * 重载{@link #cropBitmap(int, int, Bitmap.Config, File, byte[])}
     */
    public static Bitmap cropBitmap(int width,int height,Bitmap.Config bitmapConfig,byte[] data){
        return cropBitmap(width,height,bitmapConfig,null,data);
    }

    /**
     * 裁切Bitmap至小于width和height，当两值都为0（未指定）则裁小于屏幕的尺寸
     * @param width         建议宽
     * @param height        建议高
     * @param bitmapConfig  bitmap位配置
     * @param file          来源文件
     * @param data          来源字节组
     * @return 裁切后的Bitmap
     */
    @SuppressWarnings("all")
    private static Bitmap cropBitmap(int width, int height, Bitmap.Config bitmapConfig,File file,byte[] data){
        BitmapFactory.Options options=new BitmapFactory.Options();
        BitmapFactory.Options justDecodeBoundOptions=new BitmapFactory.Options();

        justDecodeBoundOptions.inJustDecodeBounds=true;
        if(file!=null) BitmapFactory.decodeFile(file.getAbsolutePath(),justDecodeBoundOptions);
        else BitmapFactory.decodeByteArray(data,0,data.length,justDecodeBoundOptions);
        //如果请求宽高非0，尝试读取指定宽高中的最低值等比缩小.都为0则尝试读取比手机屏幕小的尺寸
        if(width!=0&&height!=0){
            float widthRadio=justDecodeBoundOptions.outWidth/width;
            float heightRadio=justDecodeBoundOptions.outHeight/height;
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
        options.inPreferredConfig=bitmapConfig;
        if(options.inSampleSize>1)
            Log.d(TAG,String.format(Locale.getDefault(),"裁切:(%s,%s)-->(%s,%s)",justDecodeBoundOptions.outWidth, justDecodeBoundOptions.outHeight,
                    justDecodeBoundOptions.outWidth/options.inSampleSize,justDecodeBoundOptions.outHeight/options.inSampleSize));
        if(file!=null) return BitmapFactory.decodeFile(file.getAbsolutePath(),options);
        else return BitmapFactory.decodeByteArray(data,0,data.length,options);
    }
}