package com.fastlib.test.UrlImage.processing_state;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;

import com.fastlib.bean.StringTable;
import com.fastlib.db.And;
import com.fastlib.db.Condition;
import com.fastlib.db.DatabaseNoDataResultCallback;
import com.fastlib.db.FastDatabase;
import com.fastlib.net.DefaultDownload;
import com.fastlib.net.Request;
import com.fastlib.net.SimpleListener;
import com.fastlib.test.UrlImage.BitmapRequest;
import com.fastlib.test.UrlImage.ImageDispatchCallback;
import com.fastlib.test.UrlImage.ImageProcessingManager;
import com.fastlib.test.UrlImage.UrlImageProcessing;

import java.util.List;

/**
 * Created by sgfb on 18/1/15.
 * 图像验证过期和下载状态。如果未过期，结束任务（在{@link StateFirstLoadImageOnDisk}已经处理完）
 * 如果过期或者不存在下载后再调起状态{@link StateLoadNewImageOnDisk}
 */
public class StateDownloadImageIfExpire extends UrlImageProcessing {
    private Context mContext;

    public StateDownloadImageIfExpire(BitmapRequest request,ImageDispatchCallback callback){
        super(request, callback);
    }

    @Override
    public void handle(final ImageProcessingManager processingManager){
        final List<BitmapRequest> requestList=processingManager.getRequestList();
        final Request request=new Request("get",mRequest.getUrl());
        DefaultDownload dd=new DefaultDownload(BitmapRequest.getSaveFile(mRequest));
        StringTable lastModified= FastDatabase.getDefaultInstance(mContext)
                .addFilter(And.condition(Condition.equal(mRequest.getKey())))
                .getFirst(StringTable.class);
        if(lastModified!=null&&!TextUtils.isEmpty(lastModified.value)) {
            request.addHeader("If-Modified-Since", lastModified.value);
            System.out.println("验证服务器图像过期,如果过期重新在服务器上取:"+mRequest.getUrl());
        }
        else System.out.println("从服务器中取图像到磁盘:"+mRequest.getUrl());
        request.setDownloadable(dd);
        request.setSuppressWarning(true);
        request.setListener(new SimpleListener<String>(){

            @Override
            public void onResponseListener(Request r, String result){
                List<String> lastModifiedList=r.getReceiveHeader().get("Last-Modified");
                String lastModified=lastModifiedList==null||lastModifiedList.isEmpty()?"":lastModifiedList.get(0);

                requestList.remove(mRequest);
                saveImageLastModified(mContext,mRequest.getKey(),lastModified);
                processingManager.imageProcessStateConvert(false,new StateLoadNewImageOnDisk(mRequest,mCallback));
            }

            @Override
            public void onErrorListener(Request r, String error){
                requestList.remove(mRequest);
                if(r.getResponseStatus().code==304)
                    processingManager.imageProcessStateConvert(false,new StateLoadNewImageOnDisk(mRequest,mCallback));
            }
        });
        request.start();
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
        mContext=context;
    }

    @Override
    public void onPause(Context context) {

    }

    @Override
    public void onDestroy(Context context) {

    }
}
