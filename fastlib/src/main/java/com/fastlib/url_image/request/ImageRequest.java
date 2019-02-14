package com.fastlib.url_image.request;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.fastlib.app.module.FastActivity;
import com.fastlib.app.module.LifecycleManager;
import com.fastlib.url_image.ImageManager;
import com.fastlib.url_image.bean.ImageConfig;

/**
 * Created by sgfb on 2017/11/4.
 * Bitmap请求类
 * 建议宽高.如果没有指定宽高(width和height都是0)，载入一个小于屏幕尺寸的图像.如果指定宽高为(-1,-1),读取原图宽高
 * @param <T> 图像请求源
 */
public class ImageRequest<T> implements FastActivity.HostLifecycle{
    private T mSource;
    private boolean isCanceled;
    private int mRequestWidth;
    private int mRequestHeight;
    private int mStoreStrategy = ImageConfig.STRATEGY_STORE_SAVE_MEMORY | ImageConfig.STRATEGY_STORE_SAVE_DISK;
    private Bitmap.Config mBitmapConfig = Bitmap.Config.RGB_565;
    private CallbackParcel mCallbackParcel;
    private OnCancelListener mCancelListener;
    private Drawable mReplaceDrawable;                            //占位图
    private Drawable mErrorDrawable;                              //错误提示图
    private ViewAnimator mAnimator = new ViewAnimator() {
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

    public ImageRequest bindOnHostLifeCycle(Context context){
        LifecycleManager.registerLifecycle(context,this);
        return this;
    }

    public ImageRequest bindOnHostLifeCycle(Fragment fragment){
        LifecycleManager.registerLifecycle(fragment,this);
        return this;
    }

    public ImageRequest setReplaceDrawable(Drawable drawable){
        mReplaceDrawable=drawable;
        return this;
    }

    public Drawable getReplaceDrawable() {
        return mReplaceDrawable;
    }

    public ImageRequest setErrorDrawable(Drawable drawable){
        mErrorDrawable=drawable;
        return this;
    }

    public Drawable getErrorDrawable() {
        return mErrorDrawable;
    }

    public ViewAnimator getAnimator() {
        return mAnimator;
    }

    public void setOnCancelListener(OnCancelListener listener){
        mCancelListener=listener;
    }

    public ImageRequest setRequestWidth(int width){
        mRequestWidth=width;
        return this;
    }

    public int getRequestWidth(){
        return mRequestWidth;
    }

    public ImageRequest setRequestHeight(int height){
        mRequestHeight=height;
        return this;
    }

    public int getRequestHeight(){
        return mRequestHeight;
    }

    public Bitmap.Config getBitmapConfig() {
        return mBitmapConfig;
    }

    public ImageRequest<T> setCallbackParcel(CallbackParcel parcel){
        mCallbackParcel=parcel;
        return this;
    }

    public CallbackParcel getCallbackParcel() {
        return mCallbackParcel;
    }

    public boolean isCanceled(){
        return isCanceled;
    }

    public T getSource(){
        return mSource;
    }

    public int getStoreStrategy(){
        return mStoreStrategy;
    }

    @Override
    public void onStart(Context context) {

    }

    @Override
    public void onPause(Context context) {

    }

    @Override
    public void onDestroy(Context context) {
        cancel();
    }

    public interface ViewAnimator {
        void animator(View v);
    }

    public interface OnCancelListener{
        void canceled();
    }
}