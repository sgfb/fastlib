package com.fastlib.test.UrlImage.request;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;

import com.fastlib.test.UrlImage.BitmapRequestCallback;
import com.fastlib.test.UrlImage.FastImageConfig;

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
public abstract class BitmapRequest<T>{
    protected T mResource;
    protected int mRequestWidth;
    protected int mRequestHeight;
    protected int mStoreStrategy= FastImageConfig.STRATEGY_STORE_SAVE_MEMORY|FastImageConfig.STRATEGY_STORE_SAVE_DISK;
    protected File mSpecifiedStoreFile; //指定下载位置
    protected Bitmap.Config mBitmapConfig=Bitmap.Config.RGB_565;
    protected WeakReference<Object> mHost;  //宿主，可能是Activity或者Fragment
    protected ImageView mImageView;
    protected Drawable mReplaceDrawable; //占位图
    protected Drawable mErrorDrawable; //错误提示图
    protected ViewAnimator mAnimator=new ViewAnimator() {
        @Override
        public void animator(View v) {
            v.setAlpha(0);
            v.animate().alpha(1).setDuration(450);
        }
    };
    protected BitmapRequestCallback mCallback;

    /**
     * 唯一键值来区别与其它图像
     * @return 唯一键
     */
    public abstract String getKey();

    /**
     * 指明存储路径
     * @return 如果特殊存储路径不存在指明一个常规路径
     */
    public abstract File indicateSaveFile();

    public BitmapRequest(T from,Activity activity){
        mHost=new WeakReference<>((Object) activity);
        mResource=from;
    }

    public BitmapRequest(T from,Fragment fragment){
        mHost=new WeakReference<>((Object) fragment);
        mResource=from;
    }

    public int getRequestWidth() {
        return mRequestWidth;
    }

    public BitmapRequest setRequestWidth(int requestWidth) {
        mRequestWidth = requestWidth;
        return this;
    }

    public int getRequestHeight() {
        return mRequestHeight;
    }

    public BitmapRequest setRequestHeight(int requestHeight) {
        mRequestHeight = requestHeight;
        return this;
    }

    public File getSpecifiedStoreFile() {
        return mSpecifiedStoreFile;
    }

    public BitmapRequest setSpecifiedStoreFile(File specifiedStoreFile) {
        mSpecifiedStoreFile = specifiedStoreFile;
        return this;
    }

    public Bitmap.Config getBitmapConfig() {
        return mBitmapConfig;
    }

    public BitmapRequest setBitmapConfig(Bitmap.Config bitmapConfig) {
        mBitmapConfig = bitmapConfig;
        return this;
    }

    public int getStoreStrategy() {
        return mStoreStrategy;
    }

    public BitmapRequest setStoreStrategy(int storeStrategy) {
        mStoreStrategy = storeStrategy;
        return this;
    }

    public Object getHost() {
        return mHost!=null?mHost.get():null;
    }

    public BitmapRequest setHost(Object host) {
        mHost=new WeakReference<>(host);
        return this;
    }

    public BitmapRequest setImageView(ImageView imageView){
        //判断是强制宽高还是上一个ImageView的宽高
        if(mImageView!=null&&mImageView.getWidth()==mRequestWidth&&mImageView.getHeight()==mRequestHeight){
            mRequestWidth=0;
            mRequestHeight=0;
        }
        mImageView=imageView;
        //优先强制宽高
        if(mImageView==null){
            mRequestWidth=0;
            mRequestHeight=0;
        }
        else if(mRequestWidth==0&&mRequestHeight==0){
            mRequestWidth=mImageView.getWidth();
            mRequestHeight=mImageView.getHeight();
        }
        return this;
    }

    public ImageView getImageView(){
        return mImageView;
    }

    public BitmapRequest setCallback(BitmapRequestCallback callback){
        mCallback=callback;
        return this;
    }

    public BitmapRequestCallback getCallback(){
        return mCallback;
    }

    public T getResource(){
        return mResource;
    }

    public ViewAnimator getmAnimator() {
        return mAnimator;
    }

    public BitmapRequest setAnimator(ViewAnimator mAnimator) {
        this.mAnimator = mAnimator;
        return this;
    }

    /**
     * 完结请求逻辑
     * @param wrapper 位图
     */
    public void completeRequest(Bitmap wrapper){
        if(mImageView!=null) {
            mImageView.setImageBitmap(wrapper);
            if(mAnimator!=null) mAnimator.animator(mImageView);
        }
        if(mCallback!=null) mCallback.success(this,wrapper);
    }

    /**
     * 根据全局和单请求配置返回存储位置(部分类型不适用)
     * @return 存储位置
     */
    public File getSaveFile(){
        if(mSpecifiedStoreFile!=null) return mSpecifiedStoreFile;
        return indicateSaveFile();
    }

    public Context getContext(){
        Object host=mHost.get();
        if(host instanceof Activity)
            return (Context) host;
        else if(host instanceof Fragment)
            return ((Fragment)host).getContext();
        return null;
    }

    public int computeMaxSampleSize(int originWidth,int originHeight){
        float widthRadio= (float)originWidth/(float)mRequestWidth;
        float heightRadio=(float)originHeight/(float)mRequestHeight;
        return (int) Math.ceil(Math.max(widthRadio,heightRadio));
    }

    @Override
    public boolean equals(Object o){
        if(o==this) return true;
        if(o instanceof BitmapRequest){
            BitmapRequest other= (BitmapRequest) o;
            return mResource.equals(((BitmapRequest) o).getResource())&&
                    other.getRequestWidth()==mRequestWidth&&
                    other.getRequestHeight()==mRequestHeight;
        }
        else return false;
    }

    public interface ViewAnimator {
        void animator(View v);
    }
}