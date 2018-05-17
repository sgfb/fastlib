package com.fastlib.url_image.request;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;

import com.fastlib.url_image.ImageTarget;
import com.fastlib.url_image.Target;
import com.fastlib.url_image.callback.BitmapRequestCallback;
import com.fastlib.url_image.FastImage;
import com.fastlib.url_image.bean.FastImageConfig;
import com.fastlib.url_image.lifecycle.ActivityLifecycleCallbacksAdapter;
import com.fastlib.url_image.lifecycle.HostLifecycle;
import com.fastlib.url_image.lifecycle.LifecycleControlFragment;

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
public abstract class BitmapRequest<T> implements HostLifecycle{
    protected T mResource;
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

    public BitmapRequest(T from, Activity activity) {
        mResource = from;
        setHost(activity);
    }

    public BitmapRequest(T from, Fragment fragment) {
        mResource = from;
        setHost(fragment);
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
        return mHost != null ? mHost.get() : null;
    }

    public BitmapRequest setHost(Object host) {
        mHost = new WeakReference<>(host);
        registerLifecycle();
        return this;
    }

    public BitmapRequest setReplaceDrawable(Drawable drawable){
        mReplaceDrawable=drawable;
        return this;
    }

    public Drawable getReplaceDrawable(){
        return mReplaceDrawable;
    }

    public BitmapRequest setImageView(ImageView imageView) {
        mRequestWidth=imageView.getWidth();
        mRequestHeight=imageView.getHeight();
        mTarget = new ImageTarget(imageView,getKey());
        return this;
    }

    public Target getTarget() {
        return mTarget;
    }

    public BitmapRequest setCallback(BitmapRequestCallback callback) {
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

    public BitmapRequest setAnimator(ViewAnimator mAnimator) {
        this.mAnimator = mAnimator;
        return this;
    }

    public Drawable getErrorDrawable(){
        return mErrorDrawable;
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
        FastImage.getInstance().startRequest(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof BitmapRequest) {
            BitmapRequest other = (BitmapRequest) o;
            return getKey().equals(other.getKey()) &&
                    other.getRequestWidth() == mRequestWidth &&
                    other.getRequestHeight() == mRequestHeight &&
                    (getTarget() != null && other.getTarget() != null) &&
                    other.getTarget() == other.getTarget();
        } else return false;
    }

    public interface ViewAnimator {
        void animator(View v);
    }

    @Override
    public void onStart(Context context) {

    }

    @Override
    public void onPause(Context context) {

    }

    @Override
    public void onDestroy(Context context) {
        FastImage.getInstance().getTargetReference().remove(mTarget);
        unregisterLifecycle();
        mTarget=null;
    }

    /**
     * 注册宿主生命周期
     */
    public void registerLifecycle(){
        Object host=getHost();
        if(host!=null){
            if(host instanceof Activity){
                Activity activity= (Activity)host;
                activity.getApplication().registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
            }
            else if(host instanceof Fragment){
                Fragment fragment= (Fragment)host;
                LifecycleControlFragment controlFragment=new LifecycleControlFragment();
                controlFragment.setHostLifecycle(this);
                fragment.getChildFragmentManager()
                        .beginTransaction()
                        .add(controlFragment,"lifecycleControl")
                        .commit();
            }
        }
    }

    /**
     * 解注册宿主生命周期
     */
    public void unregisterLifecycle(){
        Object host=getHost();

        if(host!=null){
            if(host instanceof Activity){
                Activity activity= (Activity) host;
                activity.getApplication().unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
            }
            else if(host instanceof Fragment){
                Fragment fragment=(Fragment)host;
                fragment.getFragmentManager()
                        .beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        }
    }

    private Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks=new ActivityLifecycleCallbacksAdapter(){

        @Override
        public void onActivityResumed(Activity activity) {
            onStart(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            onPause(activity);
        }

        @Override
        public void onActivityDestroyed(Activity activity){
            onDestroy(activity);
        }
    };
}