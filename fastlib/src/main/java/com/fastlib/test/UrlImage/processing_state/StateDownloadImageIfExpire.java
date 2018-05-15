package com.fastlib.test.UrlImage.processing_state;

import android.content.Context;
import android.text.TextUtils;

import com.fastlib.bean.ImageFileInfo;
import com.fastlib.db.And;
import com.fastlib.db.Condition;
import com.fastlib.db.DatabaseNoDataResultCallback;
import com.fastlib.db.FastDatabase;
import com.fastlib.net.DefaultDownload;
import com.fastlib.net.Request;
import com.fastlib.net.listener.SimpleListener;
import com.fastlib.test.UrlImage.BitmapWrapper;
import com.fastlib.test.UrlImage.ImageDispatchCallback;
import com.fastlib.test.UrlImage.ImageProcessManager;
import com.fastlib.test.UrlImage.UrlImageProcessing;
import com.fastlib.test.UrlImage.request.BitmapRequest;
import com.fastlib.test.UrlImage.request.UrlBitmapRequest;

import java.util.List;

/**
 * Created by sgfb on 18/1/15.
 * 图像验证过期和下载状态。如果未过期，结束任务（在{@link StateCheckImagePrepare}已经处理完）
 * 如果过期或者不存在下载后再调起状态{@link StateLoadNewImageOnDisk}
 */
public class StateDownloadImageIfExpire extends UrlImageProcessing{
    private Request mNetRequest;

    public StateDownloadImageIfExpire(BitmapRequest request,ImageDispatchCallback callback){
        super(request, callback);
    }

    @Override
    public void handle(final ImageProcessManager processingManager){
        final UrlBitmapRequest br= (UrlBitmapRequest) mRequest;
        final List<BitmapRequest> requestList=processingManager.getRequestList();
        mNetRequest=new Request("get",br.getResource());
        DefaultDownload dd=new DefaultDownload(br.getSaveFile()).setSupportBreak(true);
        //如果文件存在再从数据库取过期时间
        ImageFileInfo imageFileInfo=mRequest.getSaveFile().exists()&&mRequest.getSaveFile().length()>0?
                FastDatabase.getDefaultInstance(br.getContext())
                .addFilter(And.condition(Condition.equal(br.getKey())))
                .getFirst(ImageFileInfo.class):null;
        if(imageFileInfo!=null&&!TextUtils.isEmpty(imageFileInfo.lastModified)) {
            mNetRequest.addHeader("If-Modified-Since", imageFileInfo.lastModified);
            System.out.println("验证服务器图像过期,如果过期重新在服务器上取:"+br.getResource());
        }
        else{
            System.out.println("从服务器中取图像到磁盘:"+br.getResource());
            dd.setSupportBreak(false);
        }
        mNetRequest.setDownloadable(dd);
        mNetRequest.setSuppressWarning(true);
        mNetRequest.setListener(new SimpleListener<String>(){

            @Override
            public void onResponseListener(Request r, String result){
                List<String> lastModifiedList=r.getReceiveHeader().get("Last-Modified");
                String lastModified=lastModifiedList==null||lastModifiedList.isEmpty()?"":lastModifiedList.get(0);

                requestList.remove(br);
                saveDownloadSuccessImageInfo(br.getContext(),br.getKey(),lastModified);
                processingManager.imageProcessStateConvert(true,StateDownloadImageIfExpire.this,new StateLoadNewImageOnDisk(br,mCallback));
            }

            @Override
            public void onErrorListener(Request r, String error){
                if(r.getResponseStatus().code==304)
                    processingManager.imageProcessStateConvert(true,StateDownloadImageIfExpire.this,new StateLoadNewImageOnDisk(br,mCallback));
                else {
                    saveDownloadErrorImageInfo(br.getContext(), br.getKey());
                    mCallback.complete(StateDownloadImageIfExpire.this,mRequest,new BitmapWrapper());
                }
                requestList.remove(br);
            }
        });
        mNetRequest.start();
    }

    /**
     * 更新图像过期时间
     * @param context 上下文
     * @param key urlImage的键
     * @param lastModified 最后修改时间
     */
    private void saveDownloadSuccessImageInfo(Context context, String key, String lastModified){
        ImageFileInfo imageFileInfo=new ImageFileInfo();
        imageFileInfo.isDownloadComplete=true;
        imageFileInfo.key=key;
        imageFileInfo.lastModified=lastModified;
        FastDatabase.getDefaultInstance(context).saveOrUpdateAsync(imageFileInfo,new DatabaseNoDataResultCallback() {
            @Override
            public void onResult(boolean success) {
                //测试功能，还没想好异步存数据库后要干啥
            }
        });
    }

    private void saveDownloadErrorImageInfo(Context context,String key){
        ImageFileInfo imageFileInfo=new ImageFileInfo();
        imageFileInfo.isDownloadComplete=false;
        imageFileInfo.key=key;
        FastDatabase.getDefaultInstance(context).saveOrUpdateAsync(imageFileInfo, new DatabaseNoDataResultCallback() {
            @Override
            public void onResult(boolean success) {
                //测试功能，还没想好异步存数据库后要干啥
            }
        });
    }

    @Override
    public void onStart(Context context){
        mNetRequest.reverseCancel();
        mNetRequest.start();
    }

    @Override
    public void onPause(Context context){
        mNetRequest.cancel();
    }

    @Override
    public void onDestroy(Context context) {
        super.onDestroy(context);
    }
}