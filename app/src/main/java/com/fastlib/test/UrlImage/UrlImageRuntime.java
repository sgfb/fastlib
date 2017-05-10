package com.fastlib.test.UrlImage;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.fastlib.R;
import com.fastlib.app.FastActivity;
import com.fastlib.app.GlobalConfig;
import com.fastlib.app.TaskAction;
import com.fastlib.app.TaskChain;
import com.fastlib.app.TaskChainHead;
import com.fastlib.net.DefaultDownload;
import com.fastlib.net.Listener;
import com.fastlib.net.Request;

import java.io.File;

/**
 * Created by sgfb on 17/2/28.
 * 从UrlImage抽离的，进行一些处理动作
 */
public class UrlImageRuntime{
    private UrlImage mImage;
    private UrlImageConfig mConfig=new UrlImageConfig();

    public UrlImageRuntime(UrlImage image){
        mImage = image;
    }

    public UrlImageRuntime setQuality(Bitmap.Config config){
        mConfig.mConfig=config;
        return this;
    }

    public void into(final ImageView imageView){
        //如果存在其他图像引用，先移除
        if(imageView.getTag(R.id.urlImage)!=null){
            UrlImage oldUrlImage= (UrlImage) imageView.getTag(R.id.urlImage);
            oldUrlImage.mReferImage.remove(imageView);
        }
        //关联当前网络图像引用
        imageView.setTag(R.id.urlImage,mImage);
        mImage.mReferImage.add(imageView);

        ((Application)imageView.getContext().getApplicationContext()).unregisterActivityLifecycleCallbacks(mImage);
        ((Application)imageView.getContext().getApplicationContext()).registerActivityLifecycleCallbacks(mImage);
        if(mImage.mBitmap!=null) //如果bitmap还存在在内存中
            imageView.setImageBitmap(mImage.mBitmap);
        else if(!mImage.isDownloading){
            File fImage=new File(mImage.mDiskPath);
            //如果图像不存在磁盘中，加入到下载队列，否则从磁盘拷入到内存中
            if(!fImage.exists())
                addRequest(fImage,imageView);
            else{
                //当前仅支持FastActivity
                if(imageView.getContext() instanceof FastActivity){
                    FastActivity fastActivity= (FastActivity) imageView.getContext();
                    fastActivity.startTasks(TaskChainHead.begin(mImage.mDiskPath).next(new TaskAction<String,Bitmap>(){
                        @Override
                        public Bitmap call(String t){
                            BitmapFactory.Options options=new BitmapFactory.Options();
                            options.inPreferredConfig=mConfig.mConfig;
                            return BitmapFactory.decodeFile(t,options); //已节省内存格式读入
                        }
                    },TaskChain.TYPE_THREAD_ON_MAIN).next(new TaskAction<Bitmap,Void>(){
                        @Override
                        public Void call(Bitmap t){
                            mImage.mBitmap=t;
                            imageView.setImageBitmap(t);
                            return null;
                        }
                    },TaskChain.TYPE_THREAD_ON_MAIN));
                }
            }
        }
    }

    /**
     * 加入到下载队列
     * @param fImage
     * @param imageView
     */
    public void addRequest(File fImage, final ImageView imageView){
        mImage.isDownloading=true;
        Request request=new Request("get",mImage.mUrl);
        request.setDownloadable(new DefaultDownload(fImage));
        request.setListener(new Listener<String>(){

            @Override
            public void onRawData(Request r, byte[] data) {

            }

            @Override
            public void onTranslateJson(Request r, String json) {

            }

            @Override
            public void onResponseListener(Request r, String result){
                mImage.isDownloading=false;
                checkOverMemory();
                into(imageView);
            }

            @Override
            public void onErrorListener(Request r, String error){
                mImage.isDownloading=false;
                if(GlobalConfig.SHOW_LOG)
                    System.out.println("下载图像异常:"+error);
            }
        });
        request.start();
    }

    /**
     * 检查是否占用了超过限额的内存,超过后尝试清理
     */
    private void checkOverMemory(){
        if(ImageLoader.getInstance().useMemory()>ImageLoader.getConfig().mMaxCacheSize)
            ImageLoader.getInstance().clear();
    }
}