package com.fastlib.url_image.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.core.util.Pair;

import com.fastlib.utils.ScreenUtils;
import com.fastlib.utils.zip.ZipFileEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by sgfb on 18/1/6.
 * 包裹了除Bitmap之外的一些数据
 */
public class BitmapWrapper{
    public Bitmap bitmap;
    public int originWidth;     //图像源宽度
    public int originHeight;    //图像源高度
    public int sampleSize;      //与真实图像缩放比
    public byte[] compressData;

    public Bitmap getThumbBitmap(int requestWidth,int requestHeight){
        int bitmapWidth=bitmap.getWidth();
        int bitmapHeight=bitmap.getHeight();
        int minRadio=Math.min(bitmapWidth/requestWidth,bitmapHeight/requestHeight);

        if(minRadio<=0) return bitmap;
        int thumbWidth=bitmapWidth/minRadio;
        int thumbHeight=bitmapHeight/minRadio;
        return Bitmap.createScaledBitmap(bitmap,thumbWidth,thumbHeight,false);
    }

    public Bitmap getBitmap(){
        if(bitmap==null&&compressData!=null) uncompress();
        return bitmap;
    }

    public void compress(){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();

        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
            compressData= ZipFileEntity.memCompress(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap.recycle();
        bitmap=null;
    }

    public void uncompress(){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPreferredConfig= Bitmap.Config.RGB_565;
        try {
            byte[] uncompress=ZipFileEntity.memUncompress(compressData);
            bitmap=BitmapFactory.decodeByteArray(uncompress,0,uncompress.length,options);
            compressData=null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算加载图像到内存中的缩放比例
     * @return 缩放比例
     */
    public static int getLocalImageScale(int requestWidth,int requestHeight,int realWidth,int realHeight){
        //如果请求宽高非0,尝试读取指定宽高中的最低值等比缩小.非0则尝试读取比手机屏幕小的尺寸
        if(requestWidth!=0&&requestHeight!=0){
            float widthRadio=(float)realWidth/(float)requestWidth;
            float heightRadio=(float)realHeight/(float)requestHeight;
            int maxRadio= (int) Math.ceil(Math.max(widthRadio,heightRadio));

            if(maxRadio>1) return maxRadio;
        }
        else{
            Pair<Integer,Integer> screenSize= ScreenUtils.getScreenSize();

            if(realWidth>screenSize.first||realHeight>screenSize.second){
                float widthRadio=realWidth/screenSize.first;
                float heightRadio=realHeight/screenSize.second;
                float maxRadio=Math.max(widthRadio,heightRadio);

                return(int) maxRadio;
            }
        }
        return 1;
    }
}