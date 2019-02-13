package com.fastlib;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.fastlib.url_image.bean.ImageConfig;
import com.fastlib.url_image.request.ResponseStatus;

/**
 * Created by sgfb on 2017/11/4.
 * Bitmap请求类
 * 如果指定宽高.按照指定宽高读取
 * 如果没有指定宽高(width和height都是0),则尝试读取ImageView宽高,如果ImageView宽高也读取不到，载入一个小于屏幕尺寸的图像.
 * 如果指定宽高为(-1,-1),读取原图宽高到内存中
 * @param <T> 图像请求源
 */
public class ImageRequest<T>{
    protected T mSource;
    protected boolean isCanceled;
    protected int mRequestWidth;
    protected int mRequestHeight;
    protected int mStoreStrategy = ImageConfig.STRATEGY_STORE_SAVE_MEMORY | ImageConfig.STRATEGY_STORE_SAVE_DISK;
    protected Bitmap.Config mBitmapConfig = Bitmap.Config.RGB_565;
    protected ResponseStatus mResponseStatus=new ResponseStatus();
    protected CallbackParcel mCallbackParcel;
    protected OnCancelListener mCancelListener;
    protected Drawable mReplaceDrawable;                            //占位图
    protected Drawable mErrorDrawable;                              //错误提示图
    protected ViewAnimator mAnimator = new ViewAnimator() {
        @Override
        public void animator(View v) {
            v.setAlpha(0);
            v.animate().alpha(1).setDuration(450);
        }
    };

    private ImageRequest(T source){
        mSource=source;
    }

    public static <T> ImageRequest<T> create(T source){
        return new ImageRequest<T>(source);
    }

    public String getName(){
        return String.format("image %s", mSource.toString());
    }

    public String getSimpleName(){
        String name=getName();
        if(name.length()>9)
            name=name.substring(name.length()-9);
        return name;
    }

    @Override
    public int hashCode() {
        return mSource.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ImageRequest){
            ImageRequest request= (ImageRequest) obj;
            return mSource.equals(request.mSource);
        }
        return false;
    }

    public void start(){
        ImageManager.getInstance().addRequest(this);
    }

    public void cancel(){
        isCanceled=true;
        if(mCancelListener!=null) mCancelListener.canceled();
    }

    public void setOnCancelListener(OnCancelListener listener){
        mCancelListener=listener;
    }

    public interface ViewAnimator {
        void animator(View v);
    }

    public interface OnCancelListener{
        void canceled();
    }
}