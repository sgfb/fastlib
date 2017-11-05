package com.fastlib.test.UrlImage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fastlib.net.DefaultDownload;
import com.fastlib.net.Downloadable;
import com.fastlib.net.NetManager;
import com.fastlib.net.Request;

import java.io.File;
import java.io.IOException;

/**
 * Created by sgfb on 2017/11/4.
 * Url Image具体请求处理
 */
public class FastImageProcess implements Runnable{
    private BitmapRequest mRequest;
    private ImageDispatchCallback mCallback;

    public FastImageProcess(BitmapRequest request, ImageDispatchCallback callback) {
        mRequest = request;
        mCallback = callback;
    }

    @Override
    public void run(){
        String key=mRequest.getKey();
        //先从内存池中寻找Bitmap
        if(ReferenceManager.getInstance().getBitmapPool().containBitmap(key)){
            mCallback.complete(mRequest,ReferenceManager.getInstance().getBitmapPool().getBitmap(key));
            return;
        }
        //开始外存寻找，寻找完后尝试连接服务器询问是否有被修改，如果有再从服务器下载最新图像
        FastImageConfig config=FastImage.getInstance().getConfig();
        File imageFile=mRequest.getSpecifiedStoreFile()==null?new File(config.mSaveFolder,key):mRequest.getSpecifiedStoreFile();

        if(imageFile.exists()&&!imageFile.isDirectory()){
            Bitmap bitmap=loadBitmapFromFile(imageFile);
            mCallback.complete(mRequest,bitmap);
            try{
                //如果服务器与外存中图像一致，结束这次请求
                if(!checkImageModified(imageFile)) return;
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        try {
            imageFile.createNewFile();
            Request imageRequest=Request.obtain("get",mRequest.getUrl()).setHadRootAddress(true).setUseFactory(false);
            DefaultDownload downloadable=new DefaultDownload(imageFile);

            downloadable.setSupportBreak(true);
            downloadable.setChangeIfHadName(mRequest.isStoreRealName());
            imageRequest.setDownloadable(downloadable);
            byte[] result=NetManager.getInstance().netRequestPromptlyBack(imageRequest);
            mCallback.complete(mRequest,loadBitmapFromFile(imageFile));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 询问服务器中图像是否被修改
     * @param file 图像文件
     * @return true被修改，false与外存中图像一致
     * @throws IOException
     */
    private boolean checkImageModified(File file)throws IOException{
        Request modifiedRequest=Request.obtain("head",mRequest.getUrl()).setUseFactory(false).setHadRootAddress(true);
        NetManager.getInstance().netRequestPromptlyBack(modifiedRequest);
        long lastModified=modifiedRequest.getLastModified();
        return file.lastModified()<lastModified;
    }

    /**
     * 从外存中读入Image并且存储到Bitmap池中
     * @param file Image文件
     * @return 读入的Bitmap
     */
    private Bitmap loadBitmapFromFile(File file){
        BitmapFactory.Options loadBoundOptions=new BitmapFactory.Options();
        BitmapFactory.Options options=new BitmapFactory.Options();
        Bitmap boundBitmap;
        Bitmap bitmap;

        loadBoundOptions.inJustDecodeBounds=true;
        boundBitmap=BitmapFactory.decodeFile(file.getAbsolutePath(),loadBoundOptions);
        int widthRatio=boundBitmap.getWidth()/mRequest.getRequestWidth();
        int heightRatio=boundBitmap.getHeight()/mRequest.getRequestHeight();
        int maxRatio=Math.max(widthRatio,heightRatio);
        if(maxRatio>1)
            options.inSampleSize=maxRatio;
        options.inPreferredConfig=mRequest.getBitmapConfig();
        bitmap=BitmapFactory.decodeFile(file.getAbsolutePath());
        if(mRequest.getStrategy()==StoreStrategy.DEFAULT) //存储Bitmap到池
            ReferenceManager.getInstance().getBitmapPool().addBitmap(mRequest.getKey(),bitmap);
        return bitmap;
    }
}