package com.fastlib;

import android.os.Environment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.net.Request;
import com.fastlib.net.SimpleListener;
import com.fastlib.test.UrlImage.BitmapRequest;
import com.fastlib.test.UrlImage.FastImage;
import com.fastlib.test.UrlImage.FastImageConfig;
import com.fastlib.test.UrlImage.ImageProcessingManager;

import java.io.File;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    ImageProcessingManager mImageProceissngManager;
    @Bind(R.id.image)
    ImageView mImage;
    @Bind(R.id.image2)
    ImageView mImage2;
    @Bind(R.id.image3)
    ImageView mImage3;

    @Override
    protected void alreadyPrepared(){
        mImageProceissngManager=new ImageProcessingManager(this);
        FastImageConfig config=FastImage.getInstance().getConfig();

        config.mSaveFolder=getExternalCacheDir();
        FastImage.getInstance().setConfig(config);
    }

    @Bind(R.id.bt)
    private void commit(){
        BitmapRequest request=new BitmapRequest();
        request.setUrl("https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=2338511376,2933014988&fm=173&s=4CC2EA1A5743754B04C41DD8020010B2&w=550&h=992&img.JPEG");
        request.setSpecifiedStoreFile(new File(Environment.getExternalStorageDirectory(),"temp.jpg"));
        mImageProceissngManager.addBitmapRequest(request,mImage);
    }

    @Bind(R.id.bt2)
    private void commit2(){
        BitmapRequest request=new BitmapRequest();
//        request.setUrl("https://static.oschina.net/uploads/space/2018/0106/134811_VkaD_347223.jpg");
        request.setUrl("https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=2338511376,2933014988&fm=173&s=4CC2EA1A5743754B04C41DD8020010B2&w=550&h=992&img.JPEG");
        request.setSpecifiedStoreFile(new File(Environment.getExternalStorageDirectory(),"temp.jpg"));
        request.setRequestWidth(100);
        request.setRequestHeight(100);
        mImageProceissngManager.addBitmapRequest(request,mImage2);
    }

    @Bind(R.id.bt3)
    private void commit3(){
        Request request=Request.obtain("head","https://static.oschina.net/uploads/space/2018/0106/134811_VkaD_347223.jpg");
        request.setListener(new SimpleListener<String>(){

            @Override
            public void onResponseListener(Request r, String result) {
                System.out.println("request complete");
            }
        });
        net(request);
    }
}