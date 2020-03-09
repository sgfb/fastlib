package com.fastlib.url_image.request;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.ImageView;

import com.fastlib.url_image.ImageTarget;
import com.fastlib.url_image.Target;
import com.fastlib.url_image.callback.BitmapRequestCallback;
import com.fastlib.url_image.FastImage;
import com.fastlib.url_image.bean.FastImageConfig;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by sgfb on 2017/11/4.
 * Bitmap请求类
 * 如果指定宽高.按照指定宽高的centerCrop读取
 * 如果没有指定宽高(width和height都是0),则尝试读取ImageView宽高,如果ImageView宽高也读取不到，载入一个小于屏幕尺寸的图像.
 * 如果指定宽高为(-1,-1),读取原图宽高到内存中
 * @param <T> 图像请求源
 */
public abstract class ImageRequest<T>{
    protected T mResource;
    protected boolean isCompressInMemory;
    protected int mRequestWidth;
    protected int mRequestHeight;
    protected int mStoreStrategy = FastImageConfig.STRATEGY_STORE_SAVE_MEMORY | FastImageConfig.STRATEGY_STORE_SAVE_DISK;
    protected File mSpecifiedStoreFile; //指定下载位置
    protected Bitmap.Config mBitmapConfig = Bitmap.Config.RGB_565;
    protected WeakReference<Object> mHost;  //宿主，可能是Activity或者Fragment
    protected Target mTarget;
    protected Drawable mReplaceDrawable; //占位图
    protected Drawable mErrorDrawable; //错误提示图
    protected ViewAnimator mAnimator = new ViewAnimator() {
        @Override
        public void animator(View v) {
            v.setAlpha(0);
            v.animate().alpha(1).setDuration(450);
        }
    };
    protected BitmapRequestCallback mCallback;
    protected ResponseStatus mResponseStatus=new ResponseStatus();

    /**
     * 唯一键值来区别与其它图像
     *
     * @return 唯一键
     */
    public abstract String getKey();

    /**
     * 指明存储路径
     *
     * @return 如果特殊存储路径不存在指明一个常规路径
     */
    public abstract File indicateSaveFile();

    public ImageRequest(T from, Activity activity) {
        mResource = from;
        setHost(activity);
    }

    public ImageRequest(T from, Fragment fragment) {
        mResource = from;
        setHost(fragment);
    }

    public int getRequestWidth() {
        return mRequestWidth;
    }

    public ImageRequest setRequestWidth(int requestWidth) {
        mRequestWidth = requestWidth;
        return this;
    }

    public int getRequestHeight() {
        return mRequestHeight;
    }

    public ImageRequest setRequestHeight(int requestHeight) {
        mRequestHeight = requestHeight;
        return this;
    }

    public File getSpecifiedStoreFile() {
        return mSpecifiedStoreFile;
    }

    public ImageRequest setSpecifiedStoreFile(File specifiedStoreFile) {
        mSpecifiedStoreFile = specifiedStoreFile;
        return this;
    }

    public Bitmap.Config getBitmapConfig() {
        return mBitmapConfig;
    }

    public ImageRequest setBitmapConfig(Bitmap.Config bitmapConfig) {
        mBitmapConfig = bitmapConfig;
        return this;
    }

    public int getStoreStrategy() {
        return mStoreStrategy;
    }

    public ImageRequest setStoreStrategy(int storeStrategy) {
        mStoreStrategy = storeStrategy;
        return this;
    }

    public Object getHost() {
        return mHost != null ? mHost.get() : null;
    }

    public ImageRequest setHost(Object host) {
        mHost = new WeakReference<>(host);
        return this;
    }

    public ImageRequest setReplaceDrawable(Drawable drawable){
        mReplaceDrawable=drawable;
        return this;
    }

    public Drawable getReplaceDrawable(){
        return mReplaceDrawable;
    }

    public ImageRequest setImageView(ImageView imageView) {
        mRequestWidth=imageView.getWidth();
        mRequestHeight=imageView.getHeight();
        mTarget = new ImageTarget(imageView,getKey());
        return this;
    }

    public Target getTarget() {
        return mTarget;
    }

    public ImageRequest setCallback(BitmapRequestCallback callback) {
        mCallback = callback;
        return this;
    }

    public BitmapRequestCallback getCallback() {
        return mCallback;
    }

    public T getResource() {
        return mResource;
    }

    public ViewAnimator getmAnimator() {
        return mAnimator;
    }

    public ImageRequest setAnimator(ViewAnimator mAnimator) {
        this.mAnimator = mAnimator;
        return this;
    }

    public Drawable getErrorDrawable(){
        return mErrorDrawable;
    }

    public ResponseStatus getResponseStatus() {
        return mResponseStatus;
    }

    public ImageRequest<T> setResponseStatus(ResponseStatus mResponseStatus) {
        this.mResponseStatus = mResponseStatus;
        return this;
    }

    public ImageRequest<T> setCompressInMemory(boolean compressInMemory){
        isCompressInMemory=compressInMemory;
        return this;
    }

    public boolean isCompressInMemory(){
        return isCompressInMemory;
    }

    /**
     * 完结请求逻辑
     * @param wrapper 位图
     */
    public void completeRequest(Bitmap wrapper) {
        if (mTarget != null) {
            if (wrapper != null) {
                mTarget.success(this,wrapper);
            } else mTarget.failure(this);
        }
        if (mCallback != null) {
            if (wrapper != null)
                mCallback.success(this, wrapper);
            else mCallback.failure(this);
        }
        mHost.clear();
        mHost=null;
    }

    /**
     * 根据全局和单请求配置返回存储位置(部分类型不适用)
     * @return 存储位置
     */
    public File getSaveFile() {
        if (mSpecifiedStoreFile != null) return mSpecifiedStoreFile;
        return indicateSaveFile();
    }

    public Context getContext() {
        Object host = mHost.get();
        if (host instanceof Activity)
            return (Context) host;
        else if (host instanceof Fragment)
            return ((Fragment) host).getContext();
        return null;
    }

    public void start() {
        FastImage.request(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof ImageRequest) {
            ImageRequest other = (ImageRequest) o;
            return getKey().equals(other.getKey()) &&
                    other.getRequestWidth() == mRequestWidth &&
                    other.getRequestHeight() == mRequestHeight &&
                    (getTarget() != null && other.getTarget() != null) &&
                    other.getTarget() == other.getTarget();
        } else return false;
    }

    public static RequestFactory host(Context context){
        return RequestFactory.host(context);
    }

    public static RequestFactory host(Activity activity){
        return RequestFactory.host(activity);
    }

    public static RequestFactory host(Fragment fragment){
        return RequestFactory.host(fragment);
    }

    public interface ViewAnimator {
        void animator(View v);
    }
}