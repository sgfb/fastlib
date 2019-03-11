package com.fastlib.image_manager.state;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.fastlib.net.NetManager;
import com.fastlib.net.Request;
import com.fastlib.image_manager.ImageManager;
import com.fastlib.db.MemoryPool;
import com.fastlib.image_manager.request.ImageRequest;
import com.fastlib.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by sgfb on 19/2/9.
 * E-mail: 602687446@qq.com
 * 远程图像预处理
 * 是否在内存、外存、一致性有效
 */
public class UrlImageCheckState extends ImageState<String>{

    public UrlImageCheckState(ImageRequest<String> request){
        super(request);
    }

    @Override
    public ImageState handle() {
        if(MemoryPool.getInstance().cacheExists(mRequest.getName()))
            return new UrlImageRenderState(mRequest);
        if(storageExists())
            return new UrlImageLoadMemoryState(mRequest);
        return new UrlImageDownloadState(mRequest);
    }

    private boolean storageExists(){
        String source=mRequest.getSource();
        File file=new File(ImageManager.getInstance().getConfig().mSaveFolder, Utils.getMd5(source,false));
        if(!file.exists()||file.length()==0) {
            Log.d(TAG,String.format(Locale.getDefault(),"文件不存在:%s",mRequest.getSimpleName()));
            return false;
        }

        //检查本地和远程的文件一致性
        Log.d(TAG,"检查远程图像一致性");
        Request request=new Request("head",source).setCustomRootAddress("");
        try {
            NetManager.getInstance().netRequestPromptlyBack(request);
            Map<String,List<String>> header=request.getReceiveHeader();
            List<String> md5List=header.get("Content-MD5");
            if(md5List!=null&&!md5List.isEmpty()){
                String remoteMd5=md5List.get(0);
                String localMd5=Base64.encodeToString(Utils.getFileVerify(file.getAbsolutePath(), Utils.FileVerifyType.MD5),Base64.DEFAULT);
                if(TextUtils.equals(remoteMd5,localMd5))
                    return true;
                else Log.d(TAG,String.format(Locale.getDefault(),"文件md5不一致:%s",mRequest.getSimpleName()));
            }
            String remoteLastModified=request.getReceiveHeader("Last-Modified");
            String contentLength=request.getReceiveHeader("Content-Length");
            SimpleDateFormat sdf=new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss zzz", Locale.ENGLISH);
            if(!TextUtils.isEmpty(remoteLastModified)){
                Date date=sdf.parse(remoteLastModified);
                if(file.lastModified()>=date.getTime()&&Long.parseLong(contentLength)==file.length())
                    return true;
                else Log.d(TAG,String.format(Locale.getDefault(),"文件过期或未下载完成:%s",mRequest.getSimpleName()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
