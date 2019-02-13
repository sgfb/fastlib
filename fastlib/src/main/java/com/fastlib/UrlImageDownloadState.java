package com.fastlib;

import com.fastlib.net.DefaultDownload;
import com.fastlib.net.NetManager;
import com.fastlib.net.Request;
import com.fastlib.net.exception.NetException;
import com.fastlib.net.listener.SimpleListener;
import com.fastlib.utils.Utils;

import java.io.File;
import java.io.IOException;
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
        System.out.println(String.format(Locale.getDefault(),"图像开始下载:%s",mRequest.getSimpleName()));
        mDownloadRequest=new Request("get",mRequest.mSource);
        mDownloadRequest.setCallbackByWorkThread(true);
        File file=new File(ImageManager.getInstance().getConfig().mSaveFolder, Utils.getMd5(mRequest.mSource,false));
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
