package com.fastlib.test.UrlImage.processing_state;

import android.content.Context;
import android.text.TextUtils;

import com.fastlib.bean.StringTable;
import com.fastlib.db.And;
import com.fastlib.db.Condition;
import com.fastlib.db.DatabaseNoDataResultCallback;
import com.fastlib.db.FastDatabase;
import com.fastlib.net.DefaultDownload;
import com.fastlib.net.Request;
import com.fastlib.net.SimpleListener;
import com.fastlib.test.UrlImage.request.BitmapRequest;
import com.fastlib.test.UrlImage.ImageDispatchCallback;
import com.fastlib.test.UrlImage.ImageProcessManager;
import com.fastlib.test.UrlImage.UrlImageProcessing;
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
        StringTable lastModified= mRequest.getSaveFile().exists()&&mRequest.getSaveFile().length()>0?
                FastDatabase.getDefaultInstance(br.getContext())
                        .addFilter(And.condition(Condition.equal(br.getKey())))
                        .getFirst(StringTable.class):null;
        if(lastModified!=null&&!TextUtils.isEmpty(lastModified.value)) {
            mNetRequest.addHeader("If-Modified-Since", lastModified.value);
            System.out.println("验证服务器图像过期,如果过期重新在服务器上取:"+br.getResource());
        }
        else System.out.println("从服务器中取图像到磁盘:"+br.getResource());
        mNetRequest.setDownloadable(dd);
        mNetRequest.setSuppressWarning(true);
        mNetRequest.setListener(new SimpleListener<String>(){

            @Override
            public void onResponseListener(Request r, String result){
                List<String> lastModifiedList=r.getReceiveHeader().get("Last-Modified");
                String lastModified=lastModifiedList==null||lastModifiedList.isEmpty()?"":lastModifiedList.get(0);

                requestList.remove(br);
                saveImageLastModified(br.getContext(),br.getKey(),lastModified);
                processingManager.imageProcessStateConvert(false,StateDownloadImageIfExpire.this,new StateLoadNewImageOnDisk(br,mCallback));
            }

            @Override
            public void onErrorListener(Request r, String error){
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
    private void saveImageLastModified(Context context,String key,String lastModified){
        StringTable st=new StringTable();
        st.key=key;
        st.value=lastModified;
        FastDatabase.getDefaultInstance(context).saveOrUpdateAsync(st, new DatabaseNoDataResultCallback() {
            @Override
            public void onResult(boolean success) {
                //测试功能，还没想好异步存数据库后要干啥
            }
        });
    }

    @Override
    public void onStart(Context context) {
        System.out.println("宿主开始生命周期，继续下载");
        mNetRequest.reverseCancel();
        mNetRequest.start();
    }

    @Override
    public void onPause(Context context){
        System.out.println("宿主暂停，暂停下载");
        mNetRequest.cancel();
    }
}