package com.fastlib.url_image.state;

import android.util.Log;

import com.fastlib.net.DefaultDownload;
import com.fastlib.net.NetManager;
import com.fastlib.net.Request;
import com.fastlib.net.exception.NetException;
import com.fastlib.net.listener.SimpleListener;
import com.fastlib.url_image.ImageManager;
import com.fastlib.url_image.request.ImageRequest;
import com.fastlib.utils.Utils;

import java.io.File;
import java.util.Locale;

/**
 * Created by sgfb on 19/2/12.
 * E-mail: 602687446@qq.com
 * 下载图片
 */
public class UrlImageDownloadState extends ImageState<String>{
    private boolean mDownloadSuccess;
    private Request mDownloadRequest;

    UrlImageDownloadState(ImageRequest<String> request) {
        super(request);
    }

    @Override
    protected ImageState handle()throws Exception{
        Log.d(TAG,String.format(Locale.getDefault(),"图像开始下载:%s",mRequest.getSimpleName()));
        mDownloadRequest=new Request("get",mRequest.getSource());
        mDownloadRequest.setCallbackByWorkThread(true).setHadRootAddress(true);
        File file=new File(ImageManager.getInstance().getConfig().mSaveFolder, Utils.getMd5(mRequest.getSource(),false));
        mDownloadRequest.setDownloadable(new DefaultDownload(file).setSupportBreak(true));
        mDownloadRequest.setListener(new SimpleListener<String>(){

            @Override
            public void onResponseListener(Request r, String result){
                mDownloadSuccess=true;
            }

            @Override
            public void onErrorListener(Request r, String error) {
                super.onErrorListener(r, error);
                mDownloadSuccess=false;
            }
        });
        NetManager.getInstance().netRequestPromptlyBack(mDownloadRequest);
        if(mDownloadSuccess) return new UrlImageLoadMemoryState(mRequest);
        else throw new NetException("download error");
    }

    @Override
    protected void onCancel() {
        super.onCancel();
        if(mDownloadRequest!=null) mDownloadRequest.cancel();
    }
}
